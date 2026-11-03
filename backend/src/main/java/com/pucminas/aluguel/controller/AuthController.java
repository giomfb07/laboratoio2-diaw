package com.pucminas.aluguel.controller;

import com.pucminas.aluguel.dto.AgenteCreateRequest;
import com.pucminas.aluguel.dto.AuthResponse;
import com.pucminas.aluguel.dto.LoginRequest;
import com.pucminas.aluguel.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final AuthService service;

    public AuthController(AuthService service) { this.service = service; }

    @PostMapping("/login")
    public AuthResponse login(@Valid @RequestBody LoginRequest req) {
        return service.login(req);
    }

    /** Cadastro de agentes (empresas e bancos). Em produção, restringir a um administrador. */
    @PostMapping("/agentes")
    @ResponseStatus(HttpStatus.CREATED)
    public AuthResponse registrarAgente(@Valid @RequestBody AgenteCreateRequest req) {
        return service.registrarAgente(req);
    }
}
