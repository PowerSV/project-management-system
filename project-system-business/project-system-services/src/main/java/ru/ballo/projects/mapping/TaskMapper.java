package ru.ballo.projects.mapping;

import ru.ballo.projects.dto.member.MemberRoleDTO;
import ru.ballo.projects.dto.task.CreateTaskDTO;
import ru.ballo.projects.dto.task.TaskDTO;
import ru.ballo.projects.models.Task;
import ru.ballo.projects.models.TeamMember;
import ru.ballo.projects.models.statuses.TaskStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

import java.util.Calendar;
import java.util.Date;

@Component
@RequiredArgsConstructor
@Log4j2
public class TaskMapper {

    private final MemberMapper memberMapper;

    public Task create(CreateTaskDTO dto) {
        log.info("Creating new task");
        Task task = new Task();
        task.setName(dto.getName());
        task.setDescription(dto.getDescription());

        task.setComplexity(dto.getComplexity());

        Calendar calendar = Calendar.getInstance();
        task.setCreationDate(calendar.getTime());
        task.setLastModified(calendar.getTime());
        if (!isDeadlineAfterCreationDate(dto.getDeadline(), task.getCreationDate(), task.getComplexity())) {
            log.error("Попытка установить дедлайн раньше чем complexity + creationDate");
            throw new IllegalArgumentException(
                    "Нельзя поставить дедлайн, если complexity + creationDate натсупает позже чем дедлайн");
        }
        task.setDeadline(dto.getDeadline());
        task.setStatus(TaskStatus.NEW);
        log.info("New task created");
        return task;
    }

    public boolean isDeadlineAfterCreationDate(Date deadline, Date creationDate, int complexity) {
        log.info("Checking if deadline is after creation date");
        Calendar deadlineDate = Calendar.getInstance();
        deadlineDate.setTime(deadline);

        Calendar minDeadlineDate = Calendar.getInstance();
        minDeadlineDate.setTime(creationDate);
        minDeadlineDate.add(Calendar.HOUR_OF_DAY, complexity);

        boolean isAfter = deadlineDate.after(minDeadlineDate);
        log.info("Deadline is after creation date: {}", isAfter);
        return isAfter;
    }

    public TaskDTO map(Task task) {
        log.info("Mapping task to DTO");
        TeamMember assignee = task.getAssignee();
        TeamMember author = task.getAuthor();
        MemberRoleDTO assigneeDTO = assignee == null ? null : memberMapper.mapToMemberRoleDTO(assignee);
        MemberRoleDTO authorDTO = author == null ? null : memberMapper.mapToMemberRoleDTO(author);
        TaskDTO taskDTO = new TaskDTO(
                task.getId(),
                task.getName(),
                task.getDescription(),
                task.getComplexity(),
                task.getCreationDate(),
                task.getLastModified(),
                task.getDeadline(),
                task.getStatus().toString(),
                assigneeDTO,
                authorDTO
        );
        log.info("Mapped task to DTO: {}", taskDTO);
        return taskDTO;
    }

}
