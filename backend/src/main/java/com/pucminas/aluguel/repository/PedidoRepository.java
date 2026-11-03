package com.pucminas.aluguel.repository;

import com.pucminas.aluguel.model.Pedido;
import com.pucminas.aluguel.model.StatusPedido;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface PedidoRepository extends JpaRepository<Pedido, Long> {
    List<Pedido> findByClienteIdOrderByDataCriacaoDesc(Long clienteId);
    List<Pedido> findAllByOrderByDataCriacaoDesc();
    List<Pedido> findByStatusOrderByDataCriacaoDesc(StatusPedido status);
    boolean existsByAutomovelId(Long automovelId);
    void deleteByClienteId(Long clienteId);
}
