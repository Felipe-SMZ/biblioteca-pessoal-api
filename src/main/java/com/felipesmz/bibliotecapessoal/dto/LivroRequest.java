package com.felipesmz.bibliotecapessoal.dto;

import com.felipesmz.bibliotecapessoal.model.enums.Status;
import jakarta.validation.constraints.*;

public class LivroRequest {

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

    @Min(value = 0, message = "O número de páginas lidas não pode ser negativo")
    private Integer paginasLidas;

    @NotNull(message = "O status não pode ser vazio")
    private Status status;

    @Min(value = 1, message = "A avaliação deve ser entre 1 e 5")
    @Max(value = 5, message = "A avaliação deve ser entre 1 e 5")
    private Integer avaliacao;

    public LivroRequest() {
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

    public Integer getPaginasLidas() {
        return paginasLidas;
    }

    public void setPaginasLidas(Integer paginasLidas) {
        this.paginasLidas = paginasLidas;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public Integer getAvaliacao() {
        return avaliacao;
    }

    public void setAvaliacao(Integer avaliacao) {
        this.avaliacao = avaliacao;
    }


}
