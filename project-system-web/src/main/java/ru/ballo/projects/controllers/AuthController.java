package ru.ballo.projects.controllers;

import ru.ballo.projects.dto.authenticate.AuthenticationRequest;
import ru.ballo.projects.dto.authenticate.AuthenticationResponse;
import ru.ballo.projects.dto.authenticate.RegisterRequest;
import ru.ballo.projects.security.config.AuthenticationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Tag(name = "AuthController", description = "Контроллер для аутентификации и регистрации")
@Log4j2
public class AuthController {

    private final AuthenticationService authenticationService;

    @Operation(summary = "Регистрация",
            description = "Позволяет пользователю зарегестрироваться")
    @PostMapping("/register")
    public ResponseEntity<AuthenticationResponse> register(
            @RequestBody @Valid RegisterRequest request) {
        log.info("Received registration request");
        return ResponseEntity.ok(authenticationService.register(request));
    }

    @Operation(summary = "Аутентификация",
            description = "Позволяет пользователю пройти аутентификацию")
    @PostMapping("/authenticate")
    public ResponseEntity<AuthenticationResponse> authenticate(
            @RequestBody @Valid AuthenticationRequest request) {
        log.info("Received authentication request");
        return ResponseEntity.ok(authenticationService.authenticate(request));
    }
}
