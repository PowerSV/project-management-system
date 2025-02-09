package ru.ballo.projects.dto.team;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

@Data
@Schema(description = "Объект для удаления сотрудника из команды")
public class RemoveMemberDTO {

    @Schema(description = "Идентификатор команды")
    @NotNull(message = "Поле teamId является обязательным")
    @Positive(message = "Принимает только положительные значения - идентификатор")
    private Long teamId;

    @Schema(description = "Идентификатор сотрудника")
    @NotNull(message = "Поле memberId является обязательным")
    @Positive(message = "Принимает только положительные значения - идентификатор")
    private Long memberId;
}
