package com.pucminas.aluguel.service;

import com.pucminas.aluguel.dto.AutomovelDto;
import com.pucminas.aluguel.exception.ApiException;
import com.pucminas.aluguel.model.*;
import com.pucminas.aluguel.repository.AutomovelRepository;
import com.pucminas.aluguel.repository.PedidoRepository;
import com.pucminas.aluguel.security.UsuarioLogado;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * O proprietário é definido pelo usuário logado: um cliente cadastra veículo próprio,
 * uma empresa ou banco agente cadastra veículo da instituição.
 */
@Service
public class AutomovelService {
    private final AutomovelRepository automoveis;
    private final PedidoRepository pedidos;
    private final UsuarioLogado logado;

    public AutomovelService(AutomovelRepository automoveis, PedidoRepository pedidos, UsuarioLogado logado) {
        this.automoveis = automoveis;
        this.pedidos = pedidos;
        this.logado = logado;
    }

    @Transactional(readOnly = true)
    public List<AutomovelDto> listar() {
        return automoveis.findAll().stream().map(AutomovelDto::from).toList();
    }

    @Transactional
    public AutomovelDto criar(AutomovelDto dto, Authentication auth) {
        if (automoveis.existsByPlaca(dto.placa().toUpperCase())) {
            throw new ApiException(HttpStatus.CONFLICT, "Já existe um automóvel com esta placa");
        }
        if (automoveis.existsByMatricula(dto.matricula())) {
            throw new ApiException(HttpStatus.CONFLICT, "Já existe um automóvel com esta matrícula");
        }
        Automovel a = new Automovel();
        copiar(dto, a);
        if (logado.isAgente(auth)) {
            Agente ag = logado.agente(auth);
            a.setTipoProprietario(ag.getTipo() == TipoAgente.BANCO ? TipoProprietario.BANCO : TipoProprietario.EMPRESA);
            a.setProprietarioId(ag.getId());
        } else {
            a.setTipoProprietario(TipoProprietario.CLIENTE);
            a.setProprietarioId(logado.cliente(auth).getId());
        }
        return AutomovelDto.from(automoveis.save(a));
    }

    @Transactional
    public AutomovelDto atualizar(Long id, AutomovelDto dto, Authentication auth) {
        Automovel a = buscarDoProprietario(id, auth);
        if (!a.getPlaca().equalsIgnoreCase(dto.placa()) && automoveis.existsByPlaca(dto.placa().toUpperCase())) {
            throw new ApiException(HttpStatus.CONFLICT, "Já existe um automóvel com esta placa");
        }
        copiar(dto, a);
        return AutomovelDto.from(automoveis.save(a));
    }

    @Transactional
    public void remover(Long id, Authentication auth) {
        Automovel a = buscarDoProprietario(id, auth);
        if (pedidos.existsByAutomovelId(id)) {
            throw new ApiException(HttpStatus.CONFLICT, "Este automóvel possui pedidos e não pode ser removido");
        }
        automoveis.delete(a);
    }

    private void copiar(AutomovelDto d, Automovel a) {
        a.setMatricula(d.matricula());
        a.setAno(d.ano());
        a.setMarca(d.marca());
        a.setModelo(d.modelo());
        a.setPlaca(d.placa().toUpperCase());
    }

    private Automovel buscarDoProprietario(Long id, Authentication auth) {
        Automovel a = automoveis.findById(id)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Automóvel não encontrado"));
        boolean dono;
        if (logado.isAgente(auth)) {
            dono = a.getTipoProprietario() != TipoProprietario.CLIENTE
                    && a.getProprietarioId().equals(logado.agente(auth).getId());
        } else {
            dono = a.getTipoProprietario() == TipoProprietario.CLIENTE
                    && a.getProprietarioId().equals(logado.cliente(auth).getId());
        }
        if (!dono) {
            throw new ApiException(HttpStatus.FORBIDDEN, "Apenas o proprietário pode alterar este automóvel");
        }
        return a;
    }
}
