package ru.ballo.projects.dto.task;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;

@Data
@NoArgsConstructor
@Schema(description = "Объект-фильтр для поиска задач")
public class TaskFilter {

    @Schema(description = "наименование задачи")
    private String keyword;

    @Schema(description = "список статусов")
    private List<String> statuses;

    @Schema(description = "идентификатор исполнителя задачи")
    private Long assigneeId;

    @Schema(description = "идентификатор автора задачи")
    private Long authorId;

    @Schema(description = "Старт периода крайнего срока задачи")
    private Date deadlinePeriodStart;

    @Schema(description = "Конец периода крайнего срока задачи")
    private Date deadlinePeriodEnd;

    @Schema(description = "Старт периода создания задачи")
    private Date creationPeriodStart;

    @Schema(description = "Старт периода создания задачи")
    private Date creationPeriodEnd;
}
