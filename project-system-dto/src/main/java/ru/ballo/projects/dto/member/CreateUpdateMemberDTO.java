package ru.ballo.projects.dto.member;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Объект для создания и обновления сотрудника")
public class CreateUpdateMemberDTO {

    @Schema(description = "Идентификатор")
    private Long id;

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
}
