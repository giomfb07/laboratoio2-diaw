package com.pucminas.aluguel.model;

import jakarta.persistence.*;

@Entity
@Table(name = "automovel")
public class Automovel {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 30) private String matricula;
    @Column(nullable = false) private Integer ano;
    @Column(nullable = false, length = 60) private String marca;
    @Column(nullable = false, length = 60) private String modelo;
    @Column(nullable = false, unique = true, length = 8) private String placa;

    /** Proprietário: cliente, empresa ou banco (referência lógica por tipo + id). */
    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_proprietario", nullable = false, length = 20)
    private TipoProprietario tipoProprietario;

    @Column(name = "proprietario_id", nullable = false)
    private Long proprietarioId;

    public Long getId() { return id; }
    public String getMatricula() { return matricula; }
    public void setMatricula(String matricula) { this.matricula = matricula; }
    public Integer getAno() { return ano; }
    public void setAno(Integer ano) { this.ano = ano; }
    public String getMarca() { return marca; }
    public void setMarca(String marca) { this.marca = marca; }
    public String getModelo() { return modelo; }
    public void setModelo(String modelo) { this.modelo = modelo; }
    public String getPlaca() { return placa; }
    public void setPlaca(String placa) { this.placa = placa; }
    public TipoProprietario getTipoProprietario() { return tipoProprietario; }
    public void setTipoProprietario(TipoProprietario t) { this.tipoProprietario = t; }
    public Long getProprietarioId() { return proprietarioId; }
    public void setProprietarioId(Long id) { this.proprietarioId = id; }
}
