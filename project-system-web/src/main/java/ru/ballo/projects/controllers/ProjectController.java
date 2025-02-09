package ru.ballo.projects.controllers;

import ru.ballo.projects.dto.project.ProjectDTO;
import ru.ballo.projects.dto.project.ProjectFilter;
import ru.ballo.projects.dto.team.TeamDTO;
import ru.ballo.projects.services.ProjectService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/project")
@RequiredArgsConstructor
@Tag(name = "ProjectController", description = "Контроллер проектов")
@Log4j2
public class ProjectController {

    private final ProjectService projectService;

    @Operation(summary = "Создание проекта",
            description = "Позволяет создать проект")
    @PostMapping(value = "/create",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ProjectDTO> create(@RequestBody ProjectDTO projectDTO) {
        log.info("Received create project request");
        return ResponseEntity.status(HttpStatus.CREATED).body(projectService.create(projectDTO));
    }

    @Operation(summary = "Обновление проекта",
            description = "Позволяет обновить имя и описание проекта")
    @PutMapping(value = "/update",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ProjectDTO> update(@RequestBody ProjectDTO projectDTO) {
        log.info("Received update project request");
        return ResponseEntity.ok().body(projectService.update(projectDTO));
    }

    @Operation(summary = "Получить проект",
            description = "Позволяет получить проект по идентификатору")
    @GetMapping(value = "/get",
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ProjectDTO> getById(@RequestParam Long id) {
        log.info("Received get project by id request");
        return ResponseEntity.ok().body(projectService.get(id));
    }

    @Operation(summary = "Получить все проекты",
            description = "Позволяет получить все существующие проекты")
    @GetMapping(value = "/all-projects",
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<ProjectDTO>> getAll() {
        log.info("Received get all projects request");
        return ResponseEntity.ok().body(projectService.getAll());
    }

    @Operation(summary = "Поиск проекта",
            description = "Позволяет получить проекты по фильтру")
    @GetMapping(value = "/search",
            produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<ProjectDTO>> searchProjects(@RequestBody ProjectFilter filter) {
        log.info("Received search projects request");
        List<ProjectDTO> foundProjects = projectService.search(filter);
        return ResponseEntity.ok().body(foundProjects);
    }

    @Operation(summary = "Обновить статус проекта",
            description = "Позволяет перевести проект в следующий статус")
    @PutMapping(value = "/update-status",
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ProjectDTO> updateStatus(@RequestParam Long id) {
        log.info("Received update status request");
        return ResponseEntity.ok().body(projectService.updateStatus(id));
    }

    @Operation(summary = "Установить команду проекту",
            description = "Позволяет задать уже существующую команду проекту или создать команду для проекта")
    @PutMapping(value = "/{id}/set-team",
            consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ProjectDTO> setTeam(@PathVariable Long id, @RequestBody TeamDTO teamDTO) {
        log.info("Received set team request");
        return ResponseEntity.ok().body(projectService.setTeam(teamDTO, id));
    }

    @Operation(summary = "Удалить проект",
            description = "Позволяет удалить проект по идентификатору из базы данных")
    @DeleteMapping(value = "/delete-from-storage",
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ProjectDTO> delete(@RequestParam Long id) {
        log.info("Received delete project request");
        return ResponseEntity.ok().body(projectService.deleteFromStorage(id));
    }
}
