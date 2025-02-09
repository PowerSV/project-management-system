package ru.ballo.projects.dto.member;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Объект для сотрудника с соответсвующей для него ролью")
public class MemberRoleDTO {

    @Schema(description = "DTO сотрудника")
    @NotNull
    private MemberDTO member;

    @Schema(description = "Роль сотрудника внутри команды",
            allowableValues = {"PROJECT_MANAGER", "ANALYST", "DEVELOPER", "TESTER"})
    @NotBlank(message = "Поле role является обязательным")
    private String role;
}
