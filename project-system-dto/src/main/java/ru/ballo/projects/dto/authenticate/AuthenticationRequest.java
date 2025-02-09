package ru.ballo.projects.dto.authenticate;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Объект для аутентификации")
public class AuthenticationRequest {

    @Schema(description = "Адрес электронной почты'")
    @NotBlank(message = "Поле email является обязательным")
    @Email(message = "Поле email должно быть валидным")
    private String email;

    @Schema(description = "Пароль")
    @NotBlank(message = "Поле password является обязательным")
    private String password;
}
