package ru.ballo.projects.controllers;

import ru.ballo.projects.dto.task.CreateTaskDTO;
import ru.ballo.projects.dto.task.TaskDTO;
import ru.ballo.projects.dto.task.TaskFilter;
import ru.ballo.projects.dto.task.UpdateTaskDTO;
import ru.ballo.projects.services.TaskService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/task")
@RequiredArgsConstructor
@Tag(name = "TaskController", description = "Контроллер задач")
@Log4j2
public class TaskController {

    private final TaskService taskService;

    @Operation(summary = "Создание задачи",
            description = "Позволяет создать задачу")
    @PostMapping(value = "/create",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<TaskDTO> create(@RequestBody @Valid CreateTaskDTO dto) {
        log.info("Received create task request");
        return ResponseEntity.ok().body(taskService.create(dto));
    }

    @Operation(summary = "Обновление задачи",
            description = "Позволяет обновить задачу")
    @PutMapping(value = "/update",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<TaskDTO> update(@RequestBody @Valid UpdateTaskDTO dto) {
        log.info("Received update task request");
        return ResponseEntity.ok().body(taskService.update(dto));
    }

    @Operation(summary = "Получить задачу",
            description = "Позволяет получить задачу по идентификатору")
    @GetMapping(value = "/get",
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<TaskDTO> getById(@RequestParam Long id) {
        log.info("Received get task by id request");
        return ResponseEntity.ok().body(taskService.get(id));
    }

    @Operation(summary = "Получить все задачи",
            description = "Позволяет получить все существующие задачи")
    @GetMapping(value = "/all-tasks",
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<TaskDTO>> getAll() {
        log.info("Received get all tasks request");
        return ResponseEntity.ok().body(taskService.getAll());
    }

    @Operation(summary = "Поиск задачи",
            description = "Позволяет получить задачи по фильтру")
    @GetMapping(value = "/search",
            produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<TaskDTO>> searchTasks(@RequestBody TaskFilter filter) {
        log.info("Received search tasks request");
        List<TaskDTO> foundTasks = taskService.search(filter);
        return ResponseEntity.ok().body(foundTasks);
    }

    @Operation(summary = "Обновить статус задачи",
            description = "Позволяет перевести задачу в следующий статус")
    @PutMapping(value = "/update-status",
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<TaskDTO> updateStatus(@RequestParam Long id) {
        log.info("Received update status request");
        return ResponseEntity.ok().body(taskService.updateStatus(id));
    }

    @Operation(summary = "Удалить задачу",
            description = "Позволяет удалить задачу из базы данных по идентификатору")
    @DeleteMapping(value = "/delete-from-storage",
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<TaskDTO> delete(@RequestParam Long id) {
        log.info("Received delete task request");
        return ResponseEntity.ok().body(taskService.deleteFromStorage(id));
    }
}
