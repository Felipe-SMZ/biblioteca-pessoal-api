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

}
