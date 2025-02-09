package ru.ballo.projects.services.impl;

import ru.ballo.projects.dto.member.MemberDTO;
import ru.ballo.projects.dto.task.CreateTaskDTO;
import ru.ballo.projects.dto.task.TaskDTO;
import ru.ballo.projects.dto.task.TaskFilter;
import ru.ballo.projects.dto.task.UpdateTaskDTO;
import ru.ballo.projects.email.EmailContext;
import ru.ballo.projects.email.service.MailSender;
import ru.ballo.projects.mapping.TaskMapper;
import ru.ballo.projects.models.*;
import ru.ballo.projects.models.statuses.MemberStatus;
import ru.ballo.projects.models.statuses.TaskStatus;
import ru.ballo.projects.repos.JpaRepos.MemberJpaRepository;
import ru.ballo.projects.repos.JpaRepos.ProjectJpaRepository;
import ru.ballo.projects.repos.JpaRepos.TaskJpaRepository;
import ru.ballo.projects.repos.specifications.TaskSpecification;
import ru.ballo.projects.services.TaskService;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
@Log4j2
public class TaskServiceImpl implements TaskService {

    private final TaskJpaRepository taskRepository;
    private final MemberJpaRepository memberRepository;
    private final ProjectJpaRepository projectRepository;
    private final MailSender emailSender;
    private final TaskMapper taskMapper;

    @Value("${spring.mail.username}")
    private String emailFrom;

    @Override
    public TaskDTO get(Long id) {
        log.info("Getting task by ID: {}", id);
        return taskRepository.findById(id)
                .map(taskMapper::map)
                .orElseThrow();
    }

    @Override
    public List<TaskDTO> getAll() {
        log.info("Getting all tasks");
        return taskRepository.findAll().stream()
                .map(taskMapper::map)
                .toList();
    }

    @Override
    public TaskDTO create(CreateTaskDTO dto) {
        log.info("Creating new task");
        Project project = projectRepository.findById(dto.getProjectId()).orElseThrow();
        Member author = getAuthor();
        TeamMember authorTeamMember = getMemberInProjectTeam(author, project);
        Task newTask = taskMapper.create(dto);
        newTask.setAuthor(authorTeamMember);

        try {
            setAssigneeOnTask(dto.getAssignee(), newTask);
        } catch (MessagingException e) {
            throw new RuntimeException(e);
        }

        newTask = taskRepository.save(newTask);
        log.info("New task created");
        return taskMapper.map(newTask);
    }

    @Deprecated
    @Override
    public TaskDTO update(CreateTaskDTO dto) {
        return null;
    }

    @Override
    public TaskDTO update(UpdateTaskDTO dto) {
        log.info("Updating task");
        Task task = taskRepository.findById(dto.getId()).orElseThrow();

        Member author = getAuthor();
        TeamMember authorTeamMember = getMemberInProjectTeam(author, task.getAuthor().getTeam().getProject());
        task.setAuthor(authorTeamMember);

        if (dto.getName() != null) {
            task.setName(dto.getName());
        }
        if (dto.getDescription() != null) {
            task.setDescription(dto.getDescription());
        }

        if (dto.getComplexity() != null) {
            task.setComplexity(dto.getComplexity());
        }
        if (dto.getDeadline() != null) {
            task.setDeadline(dto.getDeadline());
        }
        if (!taskMapper.isDeadlineAfterCreationDate(
                task.getDeadline(),
                task.getCreationDate(),
                task.getComplexity())) {
            log.error("complexity + creationDate натсупает позже чем дедлайн");
            throw new IllegalArgumentException(
                    "Нельзя поставить дедлайн, если complexity + creationDate натсупает позже чем дедлайн");
        }

        try {
            setAssigneeOnTask(dto.getAssignee(), task);
        } catch (MessagingException e) {
            throw new RuntimeException(e);
        }

        task.setLastModified(Calendar.getInstance().getTime());
        task = taskRepository.save(task);
        log.info("Task updated: {}", task.getId());
        return taskMapper.map(task);
    }

    private void setAssigneeOnTask(MemberDTO assignee, Task task) throws MessagingException {
        if (assignee != null) {
            Member newAssignee = memberRepository.findById(assignee.getId()).orElseThrow();
            if (isAssigneeDeleted(newAssignee)) {
                throw new IllegalArgumentException("Испольнитель должен иметь статус ACTIVE");
            }
            TeamMember assigneeTeamMember = getMemberInTeam(newAssignee, task.getAuthor().getTeam());
            task.setAssignee(assigneeTeamMember);


            Map<String, Object> properties = new HashMap<>();
            properties.put("name", newAssignee.getFirstName());
            properties.put("deadline", task.getDeadline());
            String authorDisplayName = task.getAuthor().getMember().getFirstName()
                    + " " + task.getAuthor().getMember().getLastName();
            properties.put("author", authorDisplayName);
            properties.put("taskName", task.getName());

            EmailContext email = EmailContext.builder()
                    .to(newAssignee.getEmail())
                    .from(emailFrom)
                    .subject("Новая задача")
                    .template("email.html")
                    .properties(properties)
                    .build();

            emailSender.send(email);

        }
    }

    private TeamMember getMemberInProjectTeam(Member member, Project project) {
        if (project.getTeam() == null) {
            throw new IllegalArgumentException(
                    "Автором либо исполнителем задачи может являться только участник проекта");
        }
        return getMemberInTeam(member, project.getTeam());
    }

    private TeamMember getMemberInTeam(Member member, Team team) {
        return team.getTeamMembers()
                .stream()
                .filter(teamMember -> teamMember.getMember().equals(member))
                .findAny()
                .orElseThrow(() -> new IllegalArgumentException(
                        "Автором либо исполнителем задачи может являться только участник проекта"));
    }

    public boolean isAssigneeDeleted(Member assignee) {
        return assignee.getStatus() == MemberStatus.DELETED;
    }

    private Member getAuthor() {
        MemberDetails principal = (MemberDetails) SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getPrincipal();
        return principal.getMember();
    }

    @Override
    public List<TaskDTO> search(TaskFilter filter) {
        Specification<Task> spec = TaskSpecification.searchByFilter(filter);
        return taskRepository.findAll(spec)
                .stream()
                .map(taskMapper::map)
                .collect(Collectors.toList());
    }

    @Override
    public TaskDTO updateStatus(Long id) {
        log.info("Updating task status. Task ID: {}", id);
        Task task = taskRepository.findById(id).orElseThrow();
        TaskStatus currentStatus = task.getStatus();
        task.setStatus(getNextStatus(currentStatus));
        task = taskRepository.save(task);
        log.info("Task status updated. Task ID: {}, New status: {}", id, task.getStatus());
        return taskMapper.map(task);
    }

    private TaskStatus getNextStatus(TaskStatus status) {
        return switch (status) {
            case NEW -> TaskStatus.IN_PROGRESS;
            case IN_PROGRESS -> TaskStatus.DONE;
            case DONE, CLOSED -> TaskStatus.CLOSED;
        };
    }

    @Override
    public TaskDTO deleteFromStorage(Long id) {
        log.info("Deleting task from storage. Task ID: {}", id);
        Task deletedTask = taskRepository.findById(id).orElseThrow();
        taskRepository.deleteTaskById(id);
        log.info("Task deleted from storage. Deleted task: {}", deletedTask.getId());
        return taskMapper.map(deletedTask);
    }
}
