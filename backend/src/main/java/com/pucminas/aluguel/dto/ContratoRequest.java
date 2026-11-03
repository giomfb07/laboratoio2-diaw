package com.pucminas.aluguel.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import java.math.BigDecimal;

/** Se "credito" for informado, o contrato de aluguel é associado a um crédito concedido pelo banco agente. */
public record ContratoRequest(@Valid Credito credito) {
    public record Credito(
            @NotNull @Positive(message = "O valor financiado deve ser maior que zero") BigDecimal valorFinanciado,
            @NotNull @PositiveOrZero(message = "A taxa não pode ser negativa") BigDecimal taxaJurosMensal,
            @NotNull @Min(value = 1, message = "Informe ao menos 1 parcela") Integer parcelas) {}
}
