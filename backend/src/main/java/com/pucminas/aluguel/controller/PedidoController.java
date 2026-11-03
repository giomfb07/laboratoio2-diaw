package com.pucminas.aluguel.controller;

import com.pucminas.aluguel.dto.*;
import com.pucminas.aluguel.model.StatusPedido;
import com.pucminas.aluguel.service.PedidoService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/pedidos")
public class PedidoController {
    private final PedidoService service;

    public PedidoController(PedidoService service) { this.service = service; }

    @PostMapping
    @PreAuthorize("hasRole('CLIENTE')")
    @ResponseStatus(HttpStatus.CREATED)
    public PedidoResponse criar(@Valid @RequestBody PedidoRequest req, Authentication auth) {
        return service.criar(req, auth);
    }

    @GetMapping
    public List<PedidoResponse> listar(@RequestParam(required = false) StatusPedido status, Authentication auth) {
        return service.listar(status, auth);
    }

    @GetMapping("/{id}")
    public PedidoResponse buscar(@PathVariable Long id, Authentication auth) { return service.buscar(id, auth); }

    @PutMapping("/{id}")
    public PedidoResponse modificar(@PathVariable Long id, @Valid @RequestBody PedidoRequest req,
                                    Authentication auth) {
        return service.modificar(id, req, auth);
    }

    @PostMapping("/{id}/cancelar")
    @PreAuthorize("hasRole('CLIENTE')")
    public PedidoResponse cancelar(@PathVariable Long id, Authentication auth) { return service.cancelar(id, auth); }

    @PostMapping("/{id}/avaliacao")
    @PreAuthorize("hasRole('AGENTE')")
    public PedidoResponse avaliar(@PathVariable Long id, @Valid @RequestBody AvaliacaoRequest req,
                                  Authentication auth) {
        return service.avaliar(id, req, auth);
    }

    @PostMapping("/{id}/contrato")
    @PreAuthorize("hasRole('AGENTE')")
    public PedidoResponse executarContrato(@PathVariable Long id,
                                           @Valid @RequestBody(required = false) ContratoRequest req,
                                           Authentication auth) {
        return service.executarContrato(id, req, auth);
    }
}
