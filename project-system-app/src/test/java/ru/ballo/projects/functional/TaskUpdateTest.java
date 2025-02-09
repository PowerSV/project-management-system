package ru.ballo.projects.functional;

import ru.ballo.projects.dto.member.MemberDTO;
import ru.ballo.projects.dto.member.MemberRoleDTO;
import ru.ballo.projects.dto.task.CreateTaskDTO;
import ru.ballo.projects.dto.task.TaskDTO;
import ru.ballo.projects.dto.task.TaskFilter;
import ru.ballo.projects.dto.task.UpdateTaskDTO;
import ru.ballo.projects.mapping.MemberMapper;
import ru.ballo.projects.mapping.TaskMapper;
import ru.ballo.projects.models.*;
import ru.ballo.projects.models.statuses.MemberRole;
import ru.ballo.projects.models.statuses.MemberStatus;
import ru.ballo.projects.models.statuses.ProjectStatus;
import ru.ballo.projects.services.MemberDetailsService;
import ru.ballo.projects.services.TaskService;
import lombok.RequiredArgsConstructor;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.transaction.annotation.Transactional;
import ru.ballo.projects.repos.JpaRepos.*;

import java.util.*;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootTest
@Transactional
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class TaskUpdateTest {

    private final TaskService underTest;
    private final TeamMemberJpaRepository teamMemberRepository;
    private final TeamJpaRepository teamRepository;
    private final MemberJpaRepository memberRepository;
    private final MemberDetailsService memberDetailsService;
    private final ProjectJpaRepository projectRepository;
    private final TaskJpaRepository taskRepository;
    private final MemberMapper memberMapper;
    private final TaskMapper taskMapper;

    private Project testProject;
    private TeamMember testTeamMember;
    private Team testTeam;
    private TaskDTO taskToUpdate;

    @BeforeAll
    public void init() {
        //создание тестового проекта
        testProject = new Project();
        testProject.setName("Test project");
        testProject.setProjectStatus(ProjectStatus.DRAFT);

        // Создание команды и сотрудника в ней
        MemberDetails memberDetails = (MemberDetails) memberDetailsService.loadUserByUsername("test@mail.ru");
        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                memberDetails,
                null,
                memberDetails.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authentication);

        Member testMember = memberRepository.findByEmail("test@mail.ru").orElseThrow();
        testTeam = new Team();
        testTeamMember = TeamMember.builder()
                .team(testTeam)
                .member(testMember)
                .role(MemberRole.TESTER)
                .build();

        testTeam.setTeamMembers(Collections.singletonList(testTeamMember));
        testProject.setTeam(testTeam);
        testTeam.setProject(testProject);

        projectRepository.save(testProject);
        teamRepository.save(testTeam);
        teamMemberRepository.save(testTeamMember);

        // создание задачи для последуюющего ее обновления
        CreateTaskDTO testTaskDTO = new CreateTaskDTO();
        testTaskDTO.setName("Название задачи");
        testTaskDTO.setProjectId(testProject.getId());
        testTaskDTO.setComplexity(5);

        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, 2024);
        testTaskDTO.setDeadline(calendar.getTime());

        taskToUpdate = underTest.create(testTaskDTO);
    }

    @AfterAll
    public void clearInitChanges() {
        taskRepository.deleteById(taskToUpdate.getId());
        teamRepository.delete(testTeam);
        projectRepository.delete(testProject);
    }

    /**
     * Обновление несуществеющей задачи
     */
    @Test
    public void updateNotExistTask() {
        UpdateTaskDTO updateTaskDTO = new UpdateTaskDTO();
        updateTaskDTO.setId(-1L);

        Assertions.assertThatThrownBy(() -> underTest.update(updateTaskDTO))
                .isInstanceOf(NoSuchElementException.class);
    }

    /**
     * Обновление без указания id задачи
     */
    @Test
    public void updateWithoutTaskId() {
        UpdateTaskDTO updateTaskDTO = new UpdateTaskDTO();

        Assertions.assertThatThrownBy(() -> underTest.update(updateTaskDTO))
                .isInstanceOf(InvalidDataAccessApiUsageException.class);
    }

    /**
     * Обновление задачи в проекте, к котрому автор не имеет отношения
     */
    @Test
    public void updateAnotherTaskId() {
        UpdateTaskDTO updateTaskDTO = new UpdateTaskDTO();
        updateTaskDTO.setId(taskRepository.findById(2L).orElseThrow().getId());

        Assertions.assertThatThrownBy(() -> underTest.update(updateTaskDTO))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Автором либо исполнителем задачи может являться только участник проекта");
    }

    /**
     * Обновление задачи c некорректными датами крайнего срока
     */
    @Test
    public void updateTaskWithInvalidDeadline() {
        UpdateTaskDTO updateTaskDto = new UpdateTaskDTO();
        updateTaskDto.setId(taskToUpdate.getId());

        updateTaskDto.setComplexity(20);
        updateTaskDto.setDeadline(new Date());

        System.out.println(taskToUpdate);

        Assertions.assertThatThrownBy(() -> underTest.update(updateTaskDto))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Нельзя поставить дедлайн, если complexity + creationDate натсупает позже чем дедлайн");

    }

    /**
     * Обновление задачи с исполнителем не относящемся к проекту
     */
    @Test
    public void updateTaskWithInvalidAssignee() {
        UpdateTaskDTO updateTaskDTO = new UpdateTaskDTO();
        updateTaskDTO.setId(taskToUpdate.getId());

        MemberDTO testMemberDto = new MemberDTO();
        testMemberDto.setId(1L);
        updateTaskDTO.setAssignee(testMemberDto);

        Assertions.assertThatThrownBy(() -> underTest.update(updateTaskDTO))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Автором либо исполнителем задачи может являться только участник проекта");

    }

    /**
     * Обновление исполнител задачи, статус которого DELETED
     */
    @Test
    public void updateTaskWithDeletedAssignee() {
        UpdateTaskDTO updateTaskDTO = new UpdateTaskDTO();
        updateTaskDTO.setId(taskToUpdate.getId());

        MemberDTO deletedMemberDto = new MemberDTO();
        TeamMember deletedTeamMember = createDeletedTeamMember();
        deletedMemberDto.setId(deletedTeamMember.getMember().getId());
        updateTaskDTO.setAssignee(deletedMemberDto);

        Assertions.assertThatThrownBy(() -> underTest.update(updateTaskDTO))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Испольнитель должен иметь статус ACTIVE");
    }

    private TeamMember createDeletedTeamMember() {
        Member deletedMember = new Member();
        deletedMember.setFirstName("Deleted Member");
        deletedMember.setLastName("Deleted");
        deletedMember.setStatus(MemberStatus.DELETED);

        TeamMember deletedTeamMember = TeamMember.builder()
                .team(testTeam)
                .member(deletedMember)
                .role(MemberRole.TESTER)
                .build();

        memberRepository.save(deletedMember);
        return teamMemberRepository.save(deletedTeamMember);
    }

    /**
     * Обновление задачи с корректными данными
     */
    @Test
    public void updateTaskWithValidData() {
        UpdateTaskDTO updateTaskDTO = new UpdateTaskDTO();
        updateTaskDTO.setId(taskToUpdate.getId());
        updateTaskDTO.setName("new test name");
        updateTaskDTO.setDescription("updated task");
        updateTaskDTO.setComplexity(10);

        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, 2025);
        updateTaskDTO.setDeadline(calendar.getTime());

        MemberDTO testMemberDto = new MemberDTO();
        testMemberDto.setId(testTeamMember.getMember().getId());
        updateTaskDTO.setAssignee(testMemberDto);

        Calendar lastModifiedDate = Calendar.getInstance();
        TaskDTO updatedTask = underTest.update(updateTaskDTO);

        MemberRoleDTO authorAndAssigneeDTO = memberMapper.mapToMemberRoleDTO(testTeamMember);

        assertThat(updatedTask.getName()).isEqualTo("new test name");
        assertThat(updatedTask.getDescription()).isEqualTo("updated task");
        assertThat(updatedTask.getStatus()).isEqualTo("NEW");
        assertThat(updatedTask.getComplexity()).isEqualTo(10L);
        assertThat(updatedTask.getLastModified()).isAfter(lastModifiedDate.getTime());
        assertThat(updatedTask.getAuthor()).isEqualTo(authorAndAssigneeDTO);
        assertThat(updatedTask.getAssignee()).isEqualTo(authorAndAssigneeDTO);
    }

    /**
     * Обновление статуса задачи
     */
    @Test
    public void updateTaskStatus() {
        TaskDTO expectedTaskDto = underTest.get(taskToUpdate.getId());
        expectedTaskDto.setStatus("IN_PROGRESS");

        TaskDTO resultTaskDto = underTest.updateStatus(taskToUpdate.getId());

        assertThat(resultTaskDto).isEqualTo(expectedTaskDto);
    }

    /**
     * Получение всех задач
     */
    @Test
    public void getAllTasksTest() {
        List<Task> tasks = taskRepository.findAll();
        List<TaskDTO> expected = tasks.stream()
                .map(taskMapper::map)
                .toList();

        List<TaskDTO> result = underTest.getAll();

        Assertions.assertThat(result).hasSize((int) taskRepository.count());
        Assertions.assertThat(result).hasSameElementsAs(expected);
    }

    /**
     * Получение задачи по id
     */
    @Test
    public void getTaskByIdTest() {
        Task task = taskRepository.findById(taskToUpdate.getId()).orElseThrow();
        TaskDTO expected = taskMapper.map(task);

        TaskDTO result = underTest.get(taskToUpdate.getId());

        Assertions.assertThat(result).isEqualTo(expected);
    }

    /**
     * Поиск задачи по пустому фильтру
     */
    @Test
    public void searchTaskWithEmptyFilter() {
        TaskFilter filter = new TaskFilter();
        List<TaskDTO> expected = underTest.getAll();

        List<TaskDTO> searchResult = underTest.search(filter);

        Assertions.assertThat(searchResult).hasSameElementsAs(expected);
    }

    /**
     * Поиск задачи по фильтру
     */
    @Test
    public void searchTask() {
        TaskFilter filter = new TaskFilter();
        filter.setStatuses(List.of("NEW", "IN_PROGRESS"));
        filter.setKeyword("название");

        List<TaskDTO> searchResult = underTest.search(filter);

        assertThat(searchResult).isNotNull();
        Assertions.assertThat(searchResult).isNotEmpty();

        boolean allStatusesMatched = searchResult.stream()
                .allMatch(task -> task.getStatus().equals("NEW") || task.getStatus().equals("IN_PROGRESS"));
        assertThat(allStatusesMatched).isTrue();

        boolean allNameMatched = searchResult.stream()
                .allMatch(task -> task.getName().toLowerCase().contains("название"));
        assertThat(allNameMatched).isTrue();
    }

    /**
     * Удаление существующей задачи
     */
    @Test
    public void deleteExistTask() {
        long tasksSizeBeforeDelete = taskRepository.count();

        assertThat(taskRepository.existsById(taskToUpdate.getId())).isTrue();
        TaskDTO deletedTask = underTest.deleteFromStorage(taskToUpdate.getId());
        assertThat(taskRepository.existsById(deletedTask.getId())).isFalse();

        long tasksSizeAfterDelete = taskRepository.count();

        assertThat(tasksSizeBeforeDelete - 1).isEqualTo(tasksSizeAfterDelete);
    }

    /**
     * Удаление несуществующей задачи
     */
    @Test
    public void deleteNotExistTask() {
        long tasksSizeBeforeDelete = taskRepository.count();

        assertThat(taskRepository.existsById(-1L)).isFalse();

        Assertions.assertThatThrownBy(() -> underTest.deleteFromStorage(-1L))
                .isInstanceOf(NoSuchElementException.class);

        long tasksSizeAfterDelete = taskRepository.count();
        assertThat(tasksSizeBeforeDelete).isEqualTo(tasksSizeAfterDelete);
    }

}
