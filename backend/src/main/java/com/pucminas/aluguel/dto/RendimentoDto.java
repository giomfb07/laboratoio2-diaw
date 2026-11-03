package com.pucminas.aluguel.dto;

import com.pucminas.aluguel.model.Rendimento;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import java.math.BigDecimal;

public record RendimentoDto(
        @NotBlank(message = "Informe a entidade empregadora") String entidadeEmpregadora,
        @NotNull(message = "Informe o valor") @PositiveOrZero(message = "O valor não pode ser negativo") BigDecimal valor) {

    public static RendimentoDto from(Rendimento r) {
        return new RendimentoDto(r.getEntidadeEmpregadora(), r.getValor());
    }
}
