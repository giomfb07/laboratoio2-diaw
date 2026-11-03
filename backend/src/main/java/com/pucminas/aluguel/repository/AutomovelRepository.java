package com.pucminas.aluguel.repository;

import com.pucminas.aluguel.model.Automovel;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AutomovelRepository extends JpaRepository<Automovel, Long> {
    boolean existsByPlaca(String placa);
    boolean existsByMatricula(String matricula);
}
