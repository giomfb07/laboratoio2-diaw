package com.pucminas.aluguel.dto;

import com.pucminas.aluguel.model.Cliente;
import java.util.List;

public record ClienteResponse(Long id, String login, String rg, String cpf, String nome,
                              String endereco, String profissao, List<RendimentoDto> rendimentos) {

    public static ClienteResponse from(Cliente c) {
        return new ClienteResponse(c.getId(), c.getUsuario().getLogin(), c.getRg(), c.getCpf(), c.getNome(),
                c.getEndereco(), c.getProfissao(),
                c.getRendimentos().stream().map(RendimentoDto::from).toList());
    }
}
