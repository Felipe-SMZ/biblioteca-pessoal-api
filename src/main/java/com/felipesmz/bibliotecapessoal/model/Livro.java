package com.felipesmz.bibliotecapessoal.model;

import com.felipesmz.bibliotecapessoal.model.enums.Avaliacao;
import com.felipesmz.bibliotecapessoal.model.enums.Status;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
public class Livro {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String titulo;

    private String autor;

    private String genero;

    private Integer totalPaginas;

    private Integer paginasLidas;

    @Enumerated(EnumType.STRING)
    private Status status;

    @Enumerated(EnumType.ORDINAL)
    private Avaliacao avaliacao;

    private LocalDateTime dataCriacao;

    @ManyToOne
    @JoinColumn(name = "usuario_id")
    private Usuario usuario;

    public Livro() {
    }

    public Livro(Long id, String titulo, String autor, String genero, Integer totalPaginas, Integer paginasLidas, Status status, Avaliacao avaliacao, LocalDateTime dataCriacao, Usuario usuario) {
        this.id = id;
        this.titulo = titulo;
        this.autor = autor;
        this.genero = genero;
        this.totalPaginas = totalPaginas;
        this.paginasLidas = paginasLidas;
        this.status = status;
        this.avaliacao = avaliacao;
        this.dataCriacao = dataCriacao;
        this.usuario = usuario;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public Avaliacao getAvaliacao() {
        return avaliacao;
    }

    public void setAvaliacao(Avaliacao avaliacao) {
        this.avaliacao = avaliacao;
    }

    public LocalDateTime getDataCriacao() {
        return dataCriacao;
    }

    public void setDataCriacao(LocalDateTime dataCriacao) {
        this.dataCriacao = dataCriacao;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }
}
