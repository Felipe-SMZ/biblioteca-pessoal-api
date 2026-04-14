package com.felipesmz.bibliotecapessoal.model.enums;

public enum Avaliacao {
    PESSIMO(1),
    RUIM(2),
    REGULAR(3),
    BOM(4),
    EXCELENTE(5);

    private final Integer valor;

    Avaliacao(Integer valor) {
        this.valor = valor;
    }

    public Integer getValor() {
        return valor;
    }
}
