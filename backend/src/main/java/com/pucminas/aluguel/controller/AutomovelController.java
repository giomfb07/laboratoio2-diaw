package com.pucminas.aluguel.controller;

import com.pucminas.aluguel.dto.AutomovelDto;
import com.pucminas.aluguel.service.AutomovelService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/automoveis")
public class AutomovelController {
    private final AutomovelService service;

    public AutomovelController(AutomovelService service) { this.service = service; }

    @GetMapping
    public List<AutomovelDto> listar() { return service.listar(); }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public AutomovelDto criar(@Valid @RequestBody AutomovelDto dto, Authentication auth) {
        return service.criar(dto, auth);
    }

    @PutMapping("/{id}")
    public AutomovelDto atualizar(@PathVariable Long id, @Valid @RequestBody AutomovelDto dto, Authentication auth) {
        return service.atualizar(id, dto, auth);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void remover(@PathVariable Long id, Authentication auth) { service.remover(id, auth); }
}
