package com.pucminas.aluguel.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record AvaliacaoRequest(
        @NotNull(message = "Informe se o pedido foi aprovado") Boolean aprovado,
        @NotBlank(message = "Escreva o parecer financeiro") @Size(max = 500) String parecer) {}
