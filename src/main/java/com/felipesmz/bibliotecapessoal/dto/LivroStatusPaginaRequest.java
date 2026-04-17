package com.felipesmz.bibliotecapessoal.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public class LivroStatusPaginaRequest {

    @NotNull(message = "O número de páginas lidas não pode ser vazio")
    @Min(value = 0, message = "O número de páginas lidas não pode ser negativo")
    private Integer paginasLidas;

    public LivroStatusPaginaRequest() {
    }

    public Integer getPaginasLidas() {
        return paginasLidas;
    }

    public void setPaginasLidas(Integer paginasLidas) {
        this.paginasLidas = paginasLidas;
    }
}
