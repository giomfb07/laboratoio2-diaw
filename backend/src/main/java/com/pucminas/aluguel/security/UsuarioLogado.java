package com.pucminas.aluguel.security;

import com.pucminas.aluguel.exception.ApiException;
import com.pucminas.aluguel.model.Agente;
import com.pucminas.aluguel.model.Cliente;
import com.pucminas.aluguel.repository.AgenteRepository;
import com.pucminas.aluguel.repository.ClienteRepository;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

/** Resolve o cliente ou agente a partir do token (subject = login). */
@Component
public class UsuarioLogado {
    private final ClienteRepository clientes;
    private final AgenteRepository agentes;

    public UsuarioLogado(ClienteRepository clientes, AgenteRepository agentes) {
        this.clientes = clientes;
        this.agentes = agentes;
    }

    public boolean isAgente(Authentication auth) {
        return auth.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_AGENTE"));
    }

    public Cliente cliente(Authentication auth) {
        return clientes.findByUsuarioLogin(auth.getName())
                .orElseThrow(() -> new ApiException(HttpStatus.FORBIDDEN, "Acesso restrito a clientes"));
    }

    public Agente agente(Authentication auth) {
        return agentes.findByUsuarioLogin(auth.getName())
                .orElseThrow(() -> new ApiException(HttpStatus.FORBIDDEN, "Acesso restrito a agentes"));
    }
}
