package ru.ballo.projects.controllers;

import ru.ballo.projects.dto.member.CreateUpdateMemberDTO;
import ru.ballo.projects.dto.member.MemberDTO;
import ru.ballo.projects.services.MemberService;
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
@RequestMapping("/member")
@RequiredArgsConstructor
@Tag(name = "MemberController", description = "Контроллер сотрудников")
@Log4j2
public class MemberController {

    private final MemberService memberService;

    @Operation(summary = "Создание сотрудника",
            description = "Позволяет создать сотрудника")
    @PostMapping(value = "/create",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<MemberDTO> create(@RequestBody @Valid CreateUpdateMemberDTO request){
        log.info("Received create member request");
        return ResponseEntity.ok().body(memberService.create(request));
    }

    @Operation(summary = "Обновление сотрудника",
            description = "Позволяет обновить поля сотрудника")
    @PutMapping(value = "/update",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<MemberDTO> update(@RequestBody @Valid CreateUpdateMemberDTO request) {
        log.info("Received update member request");
        return ResponseEntity.ok().body(memberService.update(request));
    }

    @Operation(summary = "Получить сотрудника",
            description = "Позволяет получить сотрудника по идентификатору")
    @GetMapping(value = "/get", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<MemberDTO> getById(@RequestParam Long id) {
        log.info("Received get member by id request");
        return ResponseEntity.ok().body(memberService.get(id));
    }

    @Operation(summary = "Получить всех сотрудников",
            description = "Позволяет получить всех существующих сотрудников")
    @GetMapping(value = "/all-members",
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<MemberDTO>> getAll() {
        log.info("Received get all members request");
        return ResponseEntity.ok().body(memberService.getAll());
    }

    @Operation(summary = "Поиск сотрудников",
            description = "Позволяет найти сотрудников по ключевому слову")
    @GetMapping(value = "/search",
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<MemberDTO>> searchMembers(@RequestParam String keyword) {
        log.info("Received search members request");
        List<MemberDTO> foundMembers = memberService.search(keyword);
        return ResponseEntity.ok().body(foundMembers);
    }

    @Operation(summary = "Удалить сотрудника",
            description = "Позволяет перевести сотрудника по идентификатору в статус удаленного")
    @DeleteMapping(value = "/delete",
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<MemberDTO> deleteById(@RequestParam Long id) {
        log.info("Received delete member by id request");
        return ResponseEntity.ok().body(memberService.delete(id));
    }

    @Operation(summary = "Удалить сотрудника",
            description = "Позволяет удалить сотрудника из базы данных по идентификатору")
    @DeleteMapping(value = "/delete-from-storage",
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<MemberDTO> deleteFromStorage(@RequestParam Long id) {
        log.info("Received delete member from storage request");
        return ResponseEntity.ok().body(memberService.deleteFromStorage(id));
    }
}
