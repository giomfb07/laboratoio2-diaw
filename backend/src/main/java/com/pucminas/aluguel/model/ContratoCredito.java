package com.pucminas.aluguel.model;

import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "contrato_credito")
public class ContratoCredito {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "banco_id", nullable = false)
    private Agente banco;

    @Column(name = "valor_financiado", nullable = false, precision = 12, scale = 2)
    private BigDecimal valorFinanciado;

    @Column(name = "taxa_juros_mensal", nullable = false, precision = 6, scale = 4)
    private BigDecimal taxaJurosMensal;

    @Column(nullable = false)
    private Integer parcelas;

    public Long getId() { return id; }
    public Agente getBanco() { return banco; }
    public void setBanco(Agente banco) { this.banco = banco; }
    public BigDecimal getValorFinanciado() { return valorFinanciado; }
    public void setValorFinanciado(BigDecimal v) { this.valorFinanciado = v; }
    public BigDecimal getTaxaJurosMensal() { return taxaJurosMensal; }
    public void setTaxaJurosMensal(BigDecimal t) { this.taxaJurosMensal = t; }
    public Integer getParcelas() { return parcelas; }
    public void setParcelas(Integer parcelas) { this.parcelas = parcelas; }
}
