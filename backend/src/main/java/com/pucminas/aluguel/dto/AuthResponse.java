package com.pucminas.aluguel.dto;

import com.pucminas.aluguel.model.Perfil;
import com.pucminas.aluguel.model.TipoAgente;

public record AuthResponse(String token, Perfil perfil, String nome, Long id, TipoAgente tipoAgente) {}
