package ru.ballo.projects.dto.task;

import ru.ballo.projects.dto.member.MemberDTO;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.util.Date;

@Data
@Schema(description = "Объект для создания задачи")
public class CreateTaskDTO {

    @Schema(description = "Идентификатор")
    private Long id;

    @Schema(description = "Наименование задачи (обязательное поле)")
    @NotBlank(message = "Поле name является обязательным")
    private String name;

    @Schema(description = "Описание задачи")
    private String description;

    @Schema(description = "Испольнитель задачи (обязательное поле)")
    @NotNull(message = "Поле assignee является обязательным")
    private MemberDTO assignee;

    @Schema(description = "Идентификатор проекта")
    @NotNull(message = "Поле projectId является обязательным")
    @Positive(message = "Идентификатор не может быть отрицательным")
    private Long projectId;

    @Schema(description = "Трудозатраты - оценка, сколько в часах необходимо на исполнение задачи (обязательное поле)")
    @NotNull(message = "Поле complexity является обязательным")
    @Positive(message = "Принимает только положительные значения - сколько в часах необходимо на исполнение задачи")
    private Integer complexity;

    @Schema(description = "Крайний срок - дата, когда задача должна быть исполнена. " +
            "Нельзя выбрать дату если дата меньше, чем  дата создания + трудозатраты. (Обязательное поле)")
    @NotNull(message = "Поле deadline является обязательным")
    @Future(message = "Убедитесь, что значение даты находится в будущем")
    private Date deadline;
}
