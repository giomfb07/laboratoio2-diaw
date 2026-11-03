package com.pucminas.aluguel.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import java.util.List;

public record ClienteCreateRequest(
        @NotBlank @Size(min = 3, max = 60, message = "O login deve ter entre 3 e 60 caracteres") String login,
        @NotBlank @Size(min = 6, max = 72, message = "A senha deve ter entre 6 e 72 caracteres") String senha,
        @NotBlank(message = "Informe o RG") @Size(max = 20) String rg,
        @NotBlank(message = "Informe o CPF") @Pattern(regexp = "\\d{11}", message = "O CPF deve ter 11 dígitos") String cpf,
        @NotBlank(message = "Informe o nome") @Size(max = 120) String nome,
        @NotBlank(message = "Informe o endereço") @Size(max = 255) String endereco,
        @NotBlank(message = "Informe a profissão") @Size(max = 80) String profissao,
        @Valid @Size(max = 3, message = "No máximo 3 rendimentos") List<RendimentoDto> rendimentos) {}
