package com.felipesmz.bibliotecapessoal.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public class LivroAvaliacaoRequest {

    @NotNull(message = "Campo 'avaliacao' é obrigatório")
    @Min(1)
    @Max(5)
    private Integer avaliacao;

    public LivroAvaliacaoRequest() {
    }

    public Integer getAvaliacao() {
        return avaliacao;
    }

    public void setAvaliacao(Integer avaliacao) {
        this.avaliacao = avaliacao;
    }
}
