package com.pucminas.aluguel.dto;

import com.pucminas.aluguel.model.ContratoCredito;
import com.pucminas.aluguel.model.Pedido;
import com.pucminas.aluguel.model.StatusPedido;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public record PedidoResponse(
        Long id, StatusPedido status, LocalDate dataInicio, LocalDate dataFim, BigDecimal valorTotal,
        String parecerFinanceiro, String avaliadoPor, LocalDateTime dataCriacao,
        Long clienteId, String clienteNome, AutomovelDto automovel, CreditoDto credito) {

    public record CreditoDto(String banco, BigDecimal valorFinanciado, BigDecimal taxaJurosMensal, Integer parcelas) {
        static CreditoDto from(ContratoCredito c) {
            return c == null ? null : new CreditoDto(c.getBanco().getNome(), c.getValorFinanciado(),
                    c.getTaxaJurosMensal(), c.getParcelas());
        }
    }

    public static PedidoResponse from(Pedido p) {
        return new PedidoResponse(p.getId(), p.getStatus(), p.getDataInicio(), p.getDataFim(), p.getValorTotal(),
                p.getParecerFinanceiro(),
                p.getAgenteAvaliador() == null ? null : p.getAgenteAvaliador().getNome(),
                p.getDataCriacao(), p.getCliente().getId(), p.getCliente().getNome(),
                AutomovelDto.from(p.getAutomovel()), CreditoDto.from(p.getContratoCredito()));
    }
}
