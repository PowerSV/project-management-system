package ru.ballo.projects.dto.task;

import ru.ballo.projects.dto.member.MemberDTO;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.Date;

@EqualsAndHashCode(callSuper = false)
@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Объект для обновления задачи")
public class UpdateTaskDTO {

    @Schema(description = "Идентификатор")
    @NotNull(message = "Поле id является обязательным")
    @Positive(message = "Принимает только положительные значения - идентификатор")
    private Long id;

    @Schema(description = "Наименование задачи (обязательное поле)")
    private String name;

    @Schema(description = "Описание задачи")
    private String description;

    @Schema(description = "Испольнитель задачи (обязательное поле)")
    private MemberDTO assignee;

    @Schema(description = "Трудозатраты - оценка, сколько в часах необходимо на исполнение задачи (обязательное поле)")
    @Positive(message = "Принимает только положительные значения - сколько в часах необходимо на исполнение задачи")
    Integer complexity;

    @Schema(description = "Крайний срок - дата, когда задача должна быть исполнена. " +
            "Нельзя выбрать дату если дата меньше, чем  дата создания + трудозатраты. (Обязательное поле)")
    @Future(message = "Убедитесь, что значение даты находится в будущем")
    private Date deadline;

}
