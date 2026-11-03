package com.pucminas.aluguel.controller;

import com.pucminas.aluguel.dto.ClienteCreateRequest;
import com.pucminas.aluguel.dto.ClienteResponse;
import com.pucminas.aluguel.dto.ClienteUpdateRequest;
import com.pucminas.aluguel.service.ClienteService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/clientes")
public class ClienteController {
    private final ClienteService service;

    public ClienteController(ClienteService service) { this.service = service; }

    @PostMapping   // público: cadastro prévio
    @ResponseStatus(HttpStatus.CREATED)
    public ClienteResponse criar(@Valid @RequestBody ClienteCreateRequest req) {
        return service.criar(req);
    }

    @GetMapping
    @PreAuthorize("hasRole('AGENTE')")
    public List<ClienteResponse> listar() { return service.listar(); }

    @GetMapping("/me")
    @PreAuthorize("hasRole('CLIENTE')")
    public ClienteResponse meuCadastro(Authentication auth) { return service.meuCadastro(auth); }

    @GetMapping("/{id}")
    public ClienteResponse buscar(@PathVariable Long id, Authentication auth) { return service.buscar(id, auth); }

    @PutMapping("/{id}")
    public ClienteResponse atualizar(@PathVariable Long id, @Valid @RequestBody ClienteUpdateRequest req,
                                     Authentication auth) {
        return service.atualizar(id, req, auth);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void remover(@PathVariable Long id, Authentication auth) { service.remover(id, auth); }
}
