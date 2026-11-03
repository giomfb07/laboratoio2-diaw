package com.pucminas.aluguel.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;
import java.time.LocalDate;

public record PedidoRequest(
        @NotNull(message = "Escolha um automóvel") Long automovelId,
        @NotNull(message = "Informe a data de início") LocalDate dataInicio,
        @NotNull(message = "Informe a data de término") LocalDate dataFim,
        @NotNull(message = "Informe o valor total") @Positive(message = "O valor deve ser maior que zero") BigDecimal valorTotal) {}
