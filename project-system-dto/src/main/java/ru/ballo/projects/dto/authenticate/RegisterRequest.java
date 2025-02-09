package ru.ballo.projects.dto.authenticate;

import ru.ballo.projects.dto.member.CreateUpdateMemberDTO;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@EqualsAndHashCode(callSuper = false)
@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Объект для регистрации")
public class RegisterRequest extends CreateUpdateMemberDTO {

    @Schema(description = "Имя (обязательное поле)")
    @NotBlank(message = "Поле firstName является обязательным")
    private String firstName;

    @Schema(description = "Фамилия (обязательное поле)")
    @NotBlank(message = "Поле lastName является обязательным")
    private String lastName;

    @Schema(description = "Отчество")
    private String middleName;

    @Schema(description = "Должность сотрудника")
    private String position;

    @Schema(description = "Электронная почта. Уникальное поле")
    @Email(message = "Поле email должно быть валидным")
    private String email;

    @Schema(description = "Учетная запись. Уникальное поле")
    private String account;

    @Schema(description = "Паролль (обязательное поле)")
    @NotBlank
    private String password;
}
