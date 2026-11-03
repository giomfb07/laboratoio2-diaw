package com.pucminas.aluguel.service;

import com.pucminas.aluguel.dto.*;
import com.pucminas.aluguel.exception.ApiException;
import com.pucminas.aluguel.model.*;
import com.pucminas.aluguel.repository.AutomovelRepository;
import com.pucminas.aluguel.repository.PedidoRepository;
import com.pucminas.aluguel.security.UsuarioLogado;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

/**
 * Ciclo do pedido:
 * PENDENTE -> (agente avalia) -> APROVADO | REPROVADO ; APROVADO -> (agente executa) -> CONTRATADO ;
 * o cliente pode cancelar enquanto o pedido não estiver CONTRATADO ou CANCELADO.
 */
@Service
public class PedidoService {
    private final PedidoRepository pedidos;
    private final AutomovelRepository automoveis;
    private final UsuarioLogado logado;

    public PedidoService(PedidoRepository pedidos, AutomovelRepository automoveis, UsuarioLogado logado) {
        this.pedidos = pedidos;
        this.automoveis = automoveis;
        this.logado = logado;
    }

    @Transactional
    public PedidoResponse criar(PedidoRequest req, Authentication auth) {
        Cliente cliente = logado.cliente(auth);
        validarPeriodo(req);
        Pedido p = new Pedido();
        p.setCliente(cliente);
        aplicar(p, req);
        return PedidoResponse.from(pedidos.save(p));
    }

    @Transactional(readOnly = true)
    public List<PedidoResponse> listar(StatusPedido status, Authentication auth) {
        List<Pedido> lista;
        if (logado.isAgente(auth)) {
            lista = status == null ? pedidos.findAllByOrderByDataCriacaoDesc()
                                   : pedidos.findByStatusOrderByDataCriacaoDesc(status);
        } else {
            lista = pedidos.findByClienteIdOrderByDataCriacaoDesc(logado.cliente(auth).getId());
            if (status != null) lista = lista.stream().filter(p -> p.getStatus() == status).toList();
        }
        return lista.stream().map(PedidoResponse::from).toList();
    }

    @Transactional(readOnly = true)
    public PedidoResponse buscar(Long id, Authentication auth) {
        return PedidoResponse.from(carregarComAcesso(id, auth));
    }

    @Transactional
    public PedidoResponse modificar(Long id, PedidoRequest req, Authentication auth) {
        Pedido p = carregarComAcesso(id, auth);
        if (logado.isAgente(auth)) {
            if (p.getStatus() == StatusPedido.CANCELADO || p.getStatus() == StatusPedido.CONTRATADO) {
                throw new ApiException(HttpStatus.CONFLICT, "Pedidos cancelados ou contratados não podem ser modificados");
            }
        } else if (p.getStatus() != StatusPedido.PENDENTE) {
            throw new ApiException(HttpStatus.CONFLICT, "O pedido só pode ser modificado enquanto estiver pendente");
        }
        validarPeriodo(req);
        aplicar(p, req);
        return PedidoResponse.from(pedidos.save(p));
    }

    @Transactional
    public PedidoResponse cancelar(Long id, Authentication auth) {
        Cliente cliente = logado.cliente(auth);
        Pedido p = carregarComAcesso(id, auth);
        if (!p.getCliente().getId().equals(cliente.getId())) {
            throw new ApiException(HttpStatus.FORBIDDEN, "Você só pode cancelar os seus pedidos");
        }
        if (p.getStatus() == StatusPedido.CONTRATADO || p.getStatus() == StatusPedido.CANCELADO) {
            throw new ApiException(HttpStatus.CONFLICT, "Este pedido não pode mais ser cancelado");
        }
        p.setStatus(StatusPedido.CANCELADO);
        return PedidoResponse.from(pedidos.save(p));
    }

    @Transactional
    public PedidoResponse avaliar(Long id, AvaliacaoRequest req, Authentication auth) {
        Agente agente = logado.agente(auth);
        Pedido p = pedidos.findById(id).orElseThrow(this::naoEncontrado);
        if (p.getStatus() != StatusPedido.PENDENTE) {
            throw new ApiException(HttpStatus.CONFLICT, "Apenas pedidos pendentes podem ser avaliados");
        }
        p.setStatus(req.aprovado() ? StatusPedido.APROVADO : StatusPedido.REPROVADO);
        p.setParecerFinanceiro(req.parecer());
        p.setAgenteAvaliador(agente);
        return PedidoResponse.from(pedidos.save(p));
    }

    @Transactional
    public PedidoResponse executarContrato(Long id, ContratoRequest req, Authentication auth) {
        Agente agente = logado.agente(auth);
        Pedido p = pedidos.findById(id).orElseThrow(this::naoEncontrado);
        if (p.getStatus() != StatusPedido.APROVADO) {
            throw new ApiException(HttpStatus.CONFLICT, "Apenas pedidos aprovados podem ter o contrato executado");
        }
        if (req != null && req.credito() != null) {
            if (agente.getTipo() != TipoAgente.BANCO) {
                throw new ApiException(HttpStatus.FORBIDDEN, "Somente bancos podem conceder crédito");
            }
            if (req.credito().valorFinanciado().compareTo(p.getValorTotal()) > 0) {
                throw new ApiException(HttpStatus.BAD_REQUEST, "O valor financiado não pode exceder o valor do pedido");
            }
            ContratoCredito cc = new ContratoCredito();
            cc.setBanco(agente);
            cc.setValorFinanciado(req.credito().valorFinanciado());
            cc.setTaxaJurosMensal(req.credito().taxaJurosMensal());
            cc.setParcelas(req.credito().parcelas());
            p.setContratoCredito(cc);
        }
        p.setStatus(StatusPedido.CONTRATADO);
        return PedidoResponse.from(pedidos.save(p));
    }

    // ---------- auxiliares ----------

    private Pedido carregarComAcesso(Long id, Authentication auth) {
        Pedido p = pedidos.findById(id).orElseThrow(this::naoEncontrado);
        if (!logado.isAgente(auth) && !p.getCliente().getUsuario().getLogin().equals(auth.getName())) {
            throw new ApiException(HttpStatus.FORBIDDEN, "Você não tem acesso a este pedido");
        }
        return p;
    }

    private void validarPeriodo(PedidoRequest r) {
        if (r.dataInicio().isBefore(LocalDate.now())) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "A data de início não pode estar no passado");
        }
        if (!r.dataFim().isAfter(r.dataInicio())) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "A data de término deve ser posterior à de início");
        }
    }

    private void aplicar(Pedido p, PedidoRequest r) {
        p.setAutomovel(automoveis.findById(r.automovelId())
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Automóvel não encontrado")));
        p.setDataInicio(r.dataInicio());
        p.setDataFim(r.dataFim());
        p.setValorTotal(r.valorTotal());
    }

    private ApiException naoEncontrado() {
        return new ApiException(HttpStatus.NOT_FOUND, "Pedido não encontrado");
    }
}
