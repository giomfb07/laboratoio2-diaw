package com.pucminas.aluguel.dto;

import com.pucminas.aluguel.model.TipoAgente;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record AgenteCreateRequest(
        @NotBlank @Size(min = 3, max = 60, message = "O login deve ter entre 3 e 60 caracteres") String login,
        @NotBlank @Size(min = 6, max = 72, message = "A senha deve ter entre 6 e 72 caracteres") String senha,
        @NotBlank(message = "Informe o nome") @Size(max = 120) String nome,
        @NotBlank(message = "Informe o CNPJ") @Pattern(regexp = "\\d{14}", message = "O CNPJ deve ter 14 dígitos") String cnpj,
        @NotNull(message = "Informe o tipo (EMPRESA ou BANCO)") TipoAgente tipo) {}
