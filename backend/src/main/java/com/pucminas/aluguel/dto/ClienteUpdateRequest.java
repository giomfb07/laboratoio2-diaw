package com.pucminas.aluguel.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import java.util.List;

public record ClienteUpdateRequest(
        @NotBlank(message = "Informe o RG") @Size(max = 20) String rg,
        @NotBlank(message = "Informe o CPF") @Pattern(regexp = "\\d{11}", message = "O CPF deve ter 11 dígitos") String cpf,
        @NotBlank(message = "Informe o nome") @Size(max = 120) String nome,
        @NotBlank(message = "Informe o endereço") @Size(max = 255) String endereco,
        @NotBlank(message = "Informe a profissão") @Size(max = 80) String profissao,
        @Valid @Size(max = 3, message = "No máximo 3 rendimentos") List<RendimentoDto> rendimentos) {}
