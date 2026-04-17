package com.felipesmz.bibliotecapessoal.dto;


import java.time.LocalDateTime;

public class LivroResponse {

    private Long id;
    private String titulo;
    private String autor;
    private String genero;
    private Integer totalPaginas;
    private Integer paginasLidas;
    private String status;
    private Integer avaliacao;
    private LocalDateTime dataCriacao;
    private LocalDateTime dataAtualizacao;

    public LivroResponse(Long id, String titulo, String autor, String genero, Integer totalPaginas, Integer paginasLidas, String status, Integer avaliacao, LocalDateTime dataCriacao, LocalDateTime dataAtualizacao) {
        this.id = id;
        this.titulo = titulo;
        this.autor = autor;
        this.genero = genero;
        this.totalPaginas = totalPaginas;
        this.paginasLidas = paginasLidas;
        this.status = status;
        this.avaliacao = avaliacao;
        this.dataCriacao = dataCriacao;
        this.dataAtualizacao = dataAtualizacao;
    }

    public Long getId() {
        return id;
    }

    public String getTitulo() {
        return titulo;
    }

    public String getAutor() {
        return autor;
    }

    public String getGenero() {
        return genero;
    }

    public Integer getTotalPaginas() {
        return totalPaginas;
    }

    public Integer getPaginasLidas() {
        return paginasLidas;
    }

    public String getStatus() {
        return status;
    }

    public Integer getAvaliacao() {
        return avaliacao;
    }

    public LocalDateTime getDataCriacao() {
        return dataCriacao;
    }

    public LocalDateTime getDataAtualizacao() {
        return dataAtualizacao;
    }
}
