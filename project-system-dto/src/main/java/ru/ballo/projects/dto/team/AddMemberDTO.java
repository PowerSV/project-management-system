package ru.ballo.projects.dto.team;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

@Data
@Schema(description = "Объект для добавления сотрудника в команду")
public class AddMemberDTO {

    @Schema(description = "Идентификатор команды")
    @NotNull(message = "Поле teamId является обязательным")
    @Positive(message = "Принимает только положительные значения - идентификатор")
    private Long teamId;

    @Schema(description = "Идентификатор сотрудника")
    @NotNull(message = "Поле memberId является обязательным")
    @Positive(message = "Принимает только положительные значения - идентификатор")
    private Long memberId;

    @Schema(description = "Роль сотрудника в команде",
            allowableValues = {"PROJECT_MANAGER", "ANALYST", "DEVELOPER", "TESTER"})
    @NotBlank(message = "Поле role является обязательным")
    private String role;
}
