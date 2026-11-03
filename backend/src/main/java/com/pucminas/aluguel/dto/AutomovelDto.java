package com.pucminas.aluguel.dto;

import com.pucminas.aluguel.model.Automovel;
import com.pucminas.aluguel.model.TipoProprietario;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/** Usado na entrada (id e proprietário são ignorados) e na saída. */
public record AutomovelDto(
        Long id,
        @NotBlank(message = "Informe a matrícula") @Size(max = 30) String matricula,
        @NotNull(message = "Informe o ano") @Min(value = 1950, message = "Ano inválido") @Max(value = 2100, message = "Ano inválido") Integer ano,
        @NotBlank(message = "Informe a marca") @Size(max = 60) String marca,
        @NotBlank(message = "Informe o modelo") @Size(max = 60) String modelo,
        @NotBlank(message = "Informe a placa") @Size(max = 8) String placa,
        TipoProprietario tipoProprietario,
        Long proprietarioId) {

    public static AutomovelDto from(Automovel a) {
        return new AutomovelDto(a.getId(), a.getMatricula(), a.getAno(), a.getMarca(), a.getModelo(),
                a.getPlaca(), a.getTipoProprietario(), a.getProprietarioId());
    }
}
