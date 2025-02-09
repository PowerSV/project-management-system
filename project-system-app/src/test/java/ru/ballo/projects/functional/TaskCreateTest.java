package ru.ballo.projects.functional;

import ru.ballo.projects.dto.member.MemberDTO;
import ru.ballo.projects.dto.task.CreateTaskDTO;
import ru.ballo.projects.dto.task.TaskDTO;
import ru.ballo.projects.models.*;
import ru.ballo.projects.models.statuses.MemberRole;
import ru.ballo.projects.models.statuses.MemberStatus;
import ru.ballo.projects.models.statuses.ProjectStatus;
import ru.ballo.projects.models.statuses.TaskStatus;
import ru.ballo.projects.repos.JpaRepos.MemberJpaRepository;
import ru.ballo.projects.repos.JpaRepos.ProjectJpaRepository;
import ru.ballo.projects.repos.JpaRepos.TeamJpaRepository;
import ru.ballo.projects.repos.JpaRepos.TeamMemberJpaRepository;
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

import java.util.Calendar;
import java.util.Collections;
import java.util.Date;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootTest
@Transactional
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class TaskCreateTest {

    private final TaskService underTest;
    private final TeamMemberJpaRepository teamMemberRepository;
    private final TeamJpaRepository teamRepository;
    private final MemberJpaRepository memberRepository;
    private final MemberDetailsService memberDetailsService;
    private final ProjectJpaRepository projectRepository;

    private Project testProject;
    private TeamMember testTeamMember;
    private Team testTeam;

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
    }

    @AfterAll
    public void clearInitChanges() {
        teamRepository.delete(testTeam);
        projectRepository.delete(testProject);
    }


    /**
     * Создание задачи с валидными исходными данными
     */
    @Test
    public void createTaskWithValidData() {
        CreateTaskDTO testTaskDTO = new CreateTaskDTO();
        testTaskDTO.setName("Название задачи");
        MemberDTO testMemberDto = new MemberDTO();
        testMemberDto.setId(testTeamMember.getMember().getId());
        testTaskDTO.setAssignee(testMemberDto);
        testTaskDTO.setProjectId(testProject.getId());
        testTaskDTO.setComplexity(5);

        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, 2024);
        testTaskDTO.setDeadline(calendar.getTime());

        TaskDTO createdTask = underTest.create(testTaskDTO);

        Member testMember = memberRepository.findByEmail("test@mail.ru").orElseThrow();

        assertThat(createdTask.getId()).isPositive();
        assertThat(createdTask.getStatus()).isEqualTo(TaskStatus.NEW.toString());
        assertThat(createdTask
                .getAuthor()
                .getMember()
                .getId())
                .isEqualTo(testMember.getId());
        assertThat(createdTask
                .getAssignee().getMember().getId())
                .isEqualTo(testMemberDto.getId());
        assertThat(createdTask.getName()).isEqualTo("Название задачи");
    }

    /**
     * Создание задачи без указания сложности
     */
    @Test
    public void createTaskWithoutComplexity() {
        CreateTaskDTO testTaskDTO = new CreateTaskDTO();
        MemberDTO testMemberDto = new MemberDTO();
        testMemberDto.setId(testTeamMember.getMember().getId());
        testTaskDTO.setProjectId(testProject.getId());

        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, 2024);
        testTaskDTO.setDeadline(calendar.getTime());

        Assertions.assertThatThrownBy(() -> underTest.create(testTaskDTO))
                .isInstanceOf(NullPointerException.class);

    }

    /**
     * Создание задачи без указания id проекта
     */
    @Test
    public void createTaskWithoutProjectId() {
        CreateTaskDTO testTaskDTO = new CreateTaskDTO();
        MemberDTO testMemberDto = new MemberDTO();
        testMemberDto.setId(testTeamMember.getMember().getId());

        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, 2024);
        testTaskDTO.setDeadline(calendar.getTime());

        Assertions.assertThatThrownBy(() -> underTest.create(testTaskDTO))
                .isInstanceOf(InvalidDataAccessApiUsageException.class);

    }

    /**
     * Создание задачи автором, не работающем над проектом
     */
    @Test
    public void createTaskInAnotherProject() {
        CreateTaskDTO testTaskDTO = new CreateTaskDTO();
        MemberDTO testMemberDto = new MemberDTO();
        testMemberDto.setId(testTeamMember.getMember().getId());
        testTaskDTO.setProjectId(projectRepository.findById(1L).orElseThrow().getId());

        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, 2024);
        testTaskDTO.setDeadline(calendar.getTime());

        Assertions.assertThatThrownBy(() -> underTest.create(testTaskDTO))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Автором либо исполнителем задачи может являться только участник проекта");

    }

    /**
     * Создание задачи с некорректным дедлайном
     */
    @Test
    public void createTaskWithInvalidDeadline() {
        CreateTaskDTO testTaskDTO = new CreateTaskDTO();
        testTaskDTO.setName("Название задачи");
        MemberDTO testMemberDto = new MemberDTO();
        testMemberDto.setId(testTeamMember.getMember().getId());
        testTaskDTO.setAssignee(testMemberDto);
        testTaskDTO.setProjectId(testProject.getId());
        testTaskDTO.setComplexity(5);
        testTaskDTO.setDeadline(new Date());

        Assertions.assertThatThrownBy(() -> underTest.create(testTaskDTO))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Нельзя поставить дедлайн, если complexity + creationDate натсупает позже чем дедлайн");

    }

    /**
     * Создание задачи с исполнителем не относящемся к проекту
     */
    @Test
    public void createTaskAnotherAssignee() {
        CreateTaskDTO testTaskDTO = new CreateTaskDTO();
        testTaskDTO.setName("Название задачи");
        MemberDTO testMemberDto = new MemberDTO();
        testMemberDto.setId(1L);
        testTaskDTO.setAssignee(testMemberDto);
        testTaskDTO.setProjectId(testProject.getId());
        testTaskDTO.setComplexity(5);
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, 2024);
        testTaskDTO.setDeadline(calendar.getTime());

        Assertions.assertThatThrownBy(() -> underTest.create(testTaskDTO))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Автором либо исполнителем задачи может являться только участник проекта");

    }

    /**
     * Создание задачи с исполнителем, статус которого DELETED
     */
    @Test
    public void createTaskWithDeletedAssignee() {
        CreateTaskDTO testTaskDTO = new CreateTaskDTO();
        testTaskDTO.setName("Название задачи");

        MemberDTO testDeletedMemberDto = new MemberDTO();
        TeamMember deletedMember = createDeletedTeamMember();
        testDeletedMemberDto.setId(deletedMember.getMember().getId());

        testTaskDTO.setAssignee(testDeletedMemberDto);
        testTaskDTO.setProjectId(testProject.getId());
        testTaskDTO.setComplexity(5);
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, 2024);
        testTaskDTO.setDeadline(calendar.getTime());

        Assertions.assertThatThrownBy(() -> underTest.create(testTaskDTO))
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

}
