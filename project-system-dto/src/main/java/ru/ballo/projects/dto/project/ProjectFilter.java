package ru.ballo.projects.dto.project;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@Schema(description = "Объект-фильтр для поиска проектов")
public class ProjectFilter {

    @Schema(description = "наименование проекта")
    private String keyword;

    @Schema(description = "список статусов")
    private List<String> statuses;
}
