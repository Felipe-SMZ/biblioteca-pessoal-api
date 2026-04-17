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

    public static Avaliacao fromValor(Integer valor) {
        if (valor == null) return null;
        for (Avaliacao a : Avaliacao.values()) {
            if (a.getValor().equals(valor)) return a;
        }
        throw new IllegalArgumentException("Valor de avaliação inválido: " + valor);
    }
}
