package com.pucminas.aluguel.service;

import com.pucminas.aluguel.dto.AgenteCreateRequest;
import com.pucminas.aluguel.dto.AuthResponse;
import com.pucminas.aluguel.dto.LoginRequest;
import com.pucminas.aluguel.exception.ApiException;
import com.pucminas.aluguel.model.Agente;
import com.pucminas.aluguel.model.Perfil;
import com.pucminas.aluguel.model.Usuario;
import com.pucminas.aluguel.repository.AgenteRepository;
import com.pucminas.aluguel.repository.ClienteRepository;
import com.pucminas.aluguel.repository.UsuarioRepository;
import com.pucminas.aluguel.security.TokenService;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthService {
    private final UsuarioRepository usuarios;
    private final ClienteRepository clientes;
    private final AgenteRepository agentes;
    private final PasswordEncoder encoder;
    private final TokenService tokens;

    public AuthService(UsuarioRepository usuarios, ClienteRepository clientes, AgenteRepository agentes,
                       PasswordEncoder encoder, TokenService tokens) {
        this.usuarios = usuarios;
        this.clientes = clientes;
        this.agentes = agentes;
        this.encoder = encoder;
        this.tokens = tokens;
    }

    @Transactional(readOnly = true)
    public AuthResponse login(LoginRequest req) {
        Usuario u = usuarios.findByLogin(req.login())
                .filter(x -> encoder.matches(req.senha(), x.getSenhaHash()))
                .orElseThrow(() -> new ApiException(HttpStatus.UNAUTHORIZED, "Usuário ou senha inválidos"));
        return resposta(u);
    }

    @Transactional
    public AuthResponse registrarAgente(AgenteCreateRequest req) {
        if (usuarios.existsByLogin(req.login())) {
            throw new ApiException(HttpStatus.CONFLICT, "Este login já está em uso");
        }
        if (agentes.existsByCnpj(req.cnpj())) {
            throw new ApiException(HttpStatus.CONFLICT, "Já existe um agente com este CNPJ");
        }
        Agente a = new Agente();
        a.setUsuario(new Usuario(req.login(), encoder.encode(req.senha()), Perfil.AGENTE));
        a.setNome(req.nome());
        a.setCnpj(req.cnpj());
        a.setTipo(req.tipo());
        agentes.save(a);
        return resposta(a.getUsuario());
    }

    private AuthResponse resposta(Usuario u) {
        String token = tokens.gerar(u);
        if (u.getPerfil() == Perfil.AGENTE) {
            Agente a = agentes.findByUsuarioLogin(u.getLogin()).orElseThrow();
            return new AuthResponse(token, Perfil.AGENTE, a.getNome(), a.getId(), a.getTipo());
        }
        var c = clientes.findByUsuarioLogin(u.getLogin()).orElseThrow();
        return new AuthResponse(token, Perfil.CLIENTE, c.getNome(), c.getId(), null);
    }
}
