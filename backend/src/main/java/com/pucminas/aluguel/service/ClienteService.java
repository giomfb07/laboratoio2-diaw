package com.pucminas.aluguel.service;

import com.pucminas.aluguel.dto.*;
import com.pucminas.aluguel.exception.ApiException;
import com.pucminas.aluguel.model.*;
import com.pucminas.aluguel.repository.ClienteRepository;
import com.pucminas.aluguel.repository.PedidoRepository;
import com.pucminas.aluguel.repository.UsuarioRepository;
import com.pucminas.aluguel.security.UsuarioLogado;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/** CRUD de cliente. Regra: o cliente só enxerga/altera o próprio cadastro; agentes acessam todos. */
@Service
public class ClienteService {
    private final ClienteRepository clientes;
    private final UsuarioRepository usuarios;
    private final PedidoRepository pedidos;
    private final PasswordEncoder encoder;
    private final UsuarioLogado logado;

    public ClienteService(ClienteRepository clientes, UsuarioRepository usuarios, PedidoRepository pedidos,
                          PasswordEncoder encoder, UsuarioLogado logado) {
        this.clientes = clientes;
        this.usuarios = usuarios;
        this.pedidos = pedidos;
        this.encoder = encoder;
        this.logado = logado;
    }

    @Transactional
    public ClienteResponse criar(ClienteCreateRequest r) {
        if (usuarios.existsByLogin(r.login())) {
            throw new ApiException(HttpStatus.CONFLICT, "Este login já está em uso");
        }
        if (clientes.existsByCpf(r.cpf())) {
            throw new ApiException(HttpStatus.CONFLICT, "Já existe um cliente com este CPF");
        }
        Cliente c = new Cliente();
        c.setUsuario(new Usuario(r.login(), encoder.encode(r.senha()), Perfil.CLIENTE));
        preencher(c, r.rg(), r.cpf(), r.nome(), r.endereco(), r.profissao(), r.rendimentos());
        return ClienteResponse.from(clientes.save(c));
    }

    @Transactional(readOnly = true)
    public List<ClienteResponse> listar() {
        return clientes.findAll().stream().map(ClienteResponse::from).toList();
    }

    @Transactional(readOnly = true)
    public ClienteResponse buscar(Long id, Authentication auth) {
        return ClienteResponse.from(carregarComAcesso(id, auth));
    }

    @Transactional(readOnly = true)
    public ClienteResponse meuCadastro(Authentication auth) {
        return ClienteResponse.from(logado.cliente(auth));
    }

    @Transactional
    public ClienteResponse atualizar(Long id, ClienteUpdateRequest r, Authentication auth) {
        Cliente c = carregarComAcesso(id, auth);
        if (!c.getCpf().equals(r.cpf()) && clientes.existsByCpf(r.cpf())) {
            throw new ApiException(HttpStatus.CONFLICT, "Já existe um cliente com este CPF");
        }
        preencher(c, r.rg(), r.cpf(), r.nome(), r.endereco(), r.profissao(), r.rendimentos());
        return ClienteResponse.from(clientes.save(c));
    }

    @Transactional
    public void remover(Long id, Authentication auth) {
        Cliente c = carregarComAcesso(id, auth);
        pedidos.deleteByClienteId(c.getId());
        clientes.delete(c); // cascade remove o usuário e os rendimentos
    }

    private Cliente carregarComAcesso(Long id, Authentication auth) {
        Cliente c = clientes.findById(id)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Cliente não encontrado"));
        if (!logado.isAgente(auth) && !c.getUsuario().getLogin().equals(auth.getName())) {
            throw new ApiException(HttpStatus.FORBIDDEN, "Você só pode acessar o seu próprio cadastro");
        }
        return c;
    }

    private void preencher(Cliente c, String rg, String cpf, String nome, String endereco, String profissao,
                           List<RendimentoDto> rendimentos) {
        c.setRg(rg);
        c.setCpf(cpf);
        c.setNome(nome);
        c.setEndereco(endereco);
        c.setProfissao(profissao);
        c.getRendimentos().clear();
        if (rendimentos != null) {
            if (rendimentos.size() > Cliente.MAX_RENDIMENTOS) {
                throw new ApiException(HttpStatus.BAD_REQUEST, "No máximo 3 rendimentos por cliente");
            }
            rendimentos.forEach(x ->
                    c.getRendimentos().add(new Rendimento(c, x.entidadeEmpregadora(), x.valor())));
        }
    }
}
