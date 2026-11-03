package com.pucminas.aluguel.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "pedido")
public class Pedido {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "cliente_id", nullable = false)
    private Cliente cliente;

    @ManyToOne(optional = false)
    @JoinColumn(name = "automovel_id", nullable = false)
    private Automovel automovel;

    @Column(name = "data_inicio", nullable = false) private LocalDate dataInicio;
    @Column(name = "data_fim", nullable = false) private LocalDate dataFim;

    @Column(name = "valor_total", nullable = false, precision = 12, scale = 2)
    private BigDecimal valorTotal;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private StatusPedido status = StatusPedido.PENDENTE;

    @Column(name = "parecer_financeiro", length = 500)
    private String parecerFinanceiro;

    @ManyToOne
    @JoinColumn(name = "agente_avaliador_id")
    private Agente agenteAvaliador;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "contrato_credito_id")
    private ContratoCredito contratoCredito;

    @Column(name = "data_criacao", nullable = false)
    private LocalDateTime dataCriacao = LocalDateTime.now();

    public Long getId() { return id; }
    public Cliente getCliente() { return cliente; }
    public void setCliente(Cliente cliente) { this.cliente = cliente; }
    public Automovel getAutomovel() { return automovel; }
    public void setAutomovel(Automovel automovel) { this.automovel = automovel; }
    public LocalDate getDataInicio() { return dataInicio; }
    public void setDataInicio(LocalDate d) { this.dataInicio = d; }
    public LocalDate getDataFim() { return dataFim; }
    public void setDataFim(LocalDate d) { this.dataFim = d; }
    public BigDecimal getValorTotal() { return valorTotal; }
    public void setValorTotal(BigDecimal v) { this.valorTotal = v; }
    public StatusPedido getStatus() { return status; }
    public void setStatus(StatusPedido status) { this.status = status; }
    public String getParecerFinanceiro() { return parecerFinanceiro; }
    public void setParecerFinanceiro(String p) { this.parecerFinanceiro = p; }
    public Agente getAgenteAvaliador() { return agenteAvaliador; }
    public void setAgenteAvaliador(Agente a) { this.agenteAvaliador = a; }
    public ContratoCredito getContratoCredito() { return contratoCredito; }
    public void setContratoCredito(ContratoCredito c) { this.contratoCredito = c; }
    public LocalDateTime getDataCriacao() { return dataCriacao; }
}
