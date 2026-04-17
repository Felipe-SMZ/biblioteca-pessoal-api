package com.felipesmz.bibliotecapessoal.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class LivroAtualizarRequest {

    @NotBlank(message = "O título não pode ser vazio")
    @Size(min = 2, max = 100, message = "O título deve conter entre 2 e 100 caracteres")
    private String titulo;

    @NotBlank(message = "O autor não pode ser vazio")
    @Size(min = 2, max = 100, message = "O autor deve conter entre 2 e 100 caracteres")
    private String autor;

    @NotBlank(message = "O gênero não pode ser vazio")
    @Size(min = 2, max = 100, message = "O gênero deve conter entre 2 e 100 caracteres")
    private String genero;

    @NotNull(message = "O número de páginas não pode ser vazio")
    @Min(value = 1, message = "O número de páginas deve ser maior que zero")
    private Integer totalPaginas;

    public LivroAtualizarRequest() {
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getAutor() {
        return autor;
    }

    public void setAutor(String autor) {
        this.autor = autor;
    }

    public String getGenero() {
        return genero;
    }

    public void setGenero(String genero) {
        this.genero = genero;
    }

    public Integer getTotalPaginas() {
        return totalPaginas;
    }

    public void setTotalPaginas(Integer totalPaginas) {
        this.totalPaginas = totalPaginas;
    }
}
