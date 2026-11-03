package com.pucminas.aluguel.model;

public enum StatusPedido {
    PENDENTE,    // introduzido pelo cliente, aguardando análise financeira
    APROVADO,    // parecer financeiro positivo; aguarda execução do contrato
    REPROVADO,   // parecer financeiro negativo
    CONTRATADO,  // contrato executado
    CANCELADO    // cancelado pelo cliente
}
