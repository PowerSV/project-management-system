package ru.ballo.projects.dto.authenticate;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Объект ответа с токеном")
public class AuthenticationResponse {
    @Schema(description = "Поле jwt токена")
    private String token;
}
