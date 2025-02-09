package ru.ballo.projects.controllers;

import ru.ballo.projects.dto.team.AddMemberDTO;
import ru.ballo.projects.dto.team.RemoveMemberDTO;
import ru.ballo.projects.dto.team.TeamDTO;
import ru.ballo.projects.services.TeamService;
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
@RequestMapping("/team")
@RequiredArgsConstructor
@Tag(name = "TeamController", description = "Контроллер команд")
@Log4j2
public class TeamController {

    private final TeamService teamService;

    @Operation(summary = "Создание команды",
            description = "Позволяет создать команду")
    @PostMapping(value = "/create",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<TeamDTO> create(@RequestBody TeamDTO teamDTO) {
        log.info("Received create team request");
        return ResponseEntity.ok().body(teamService.create(teamDTO));
    }

    @Operation(summary = "Обновление команды",
            description = "Позволяет обновить команду, назначить существующий проект и создать новую команду")
    @PutMapping(value = "/update",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<TeamDTO> update(@RequestBody TeamDTO dto) {
        log.info("Received update team request");
        return ResponseEntity.ok().body(teamService.update(dto));
    }

    @Operation(summary = "Получение команды",
            description = "Позволяет получить команду по идентификатору")
    @GetMapping(value = "/get",
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<TeamDTO> getById(@RequestParam Long id) {
        log.info("Received get team by id request");
        return ResponseEntity.ok().body(teamService.get(id));
    }

    @Operation(summary = "Получение всех команды",
            description = "Позволяет получить все существующие команды")
    @GetMapping(value = "/all-teams",
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<TeamDTO>> getAll() {
        log.info("Received get all teams request");
        return ResponseEntity.ok().body(teamService.getAll());
    }

    @Operation(summary = "Добавить сотрудника в команду",
            description = "Позволяет добавить сотрудника в команду и задать роль в команде")
    @PutMapping(value = "/add-member",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<TeamDTO> addMember(
            @RequestBody @Valid AddMemberDTO addMemberDTO) {
        log.info("Received add member to team request");
        return ResponseEntity.ok().body(teamService.addMember(addMemberDTO));
    }

    @Operation(summary = "Удалить сотрудника из команды",
            description = "Позволяет удалить сотрудника из команды")
    @PutMapping(value = "/remove-member",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<TeamDTO> removeMember(
            @RequestBody @Valid RemoveMemberDTO removeMemberDTO) {
        log.info("Received remove member from team request");
        return ResponseEntity.ok().body(teamService.removeMember(removeMemberDTO));
    }

    @Operation(summary = "Удалить команду",
            description = "Позволяет удалить команду из базы данных по идентификатору")
    @DeleteMapping(value = "/delete-from-storage",
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<TeamDTO> delete(@RequestParam Long id) {
        log.info("Received delete team request");
        return ResponseEntity.ok().body(teamService.deleteFromStorage(id));
    }
}
