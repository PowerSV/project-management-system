package ru.ballo.projects.dto.member;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "Объект для отображения сотрудника")
public class MemberDTO {

    @Schema(description = "Идентификатор")
    private Long id;

    @Schema(description = "Отображаемое имя (ФИО)")
    private String displayName;

    @Schema(description = "Должность сотрудника")
    private String position;

    @Schema(description = "Электронная почта. Уникальное поле")
    private String email;

    @Schema(description = "Статус сотрудника")
    private String status;
}
