package ru.ballo.projects.dto.task;

import ru.ballo.projects.dto.member.MemberRoleDTO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Объект для отображения задачи")
public class TaskDTO {

    @Schema(description = "Идентификатор")
    private Long id;

    @Schema(description = "Наименование задачи")
    private String name;

    @Schema(description = "Описание задачи")
    private String description;

    @Schema(description = "Трудозатраты - оценка, сколько в часах необходимо на исполнение задачи")
    private Integer complexity;

    @Schema(description = "Дата создания задачи")
    private Date creationDate;

    @Schema(description = "Дата последнего изменения задачи")
    private Date lastModified;

    @Schema(description = "Крайний срок - дата, когда задача должна быть исполнена")
    private Date deadline;

    @Schema(description = "Статус задачи")
    private String status;

    @Schema(description = "Испольнитель задачи")
    private MemberRoleDTO assignee;

    @Schema(description = "Автор задачи")
    private MemberRoleDTO author;
}
