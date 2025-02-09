package ru.ballo.projects.dto.project;

import ru.ballo.projects.dto.team.TeamDTO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Объект для взаимодействия с проектами")
public class ProjectDTO {

    @Schema(description = "Идентификатор")
    private Long id;

    @Schema(description = "Наименование проекта (обязательное поле)")
    private String name;

    @Schema(description = "Описание проекта")
    private String description;

    @Schema(description = "Статус",
            allowableValues = {"DRAFT", "IN_DEVELOPMENT", "IN_TESTING", "COMPLETED"})
    private String status;

    @Schema(description = "Команда проекта")
    private TeamDTO teamDTO;
}
