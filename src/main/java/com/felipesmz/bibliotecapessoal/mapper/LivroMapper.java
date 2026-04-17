package com.felipesmz.bibliotecapessoal.mapper;

import com.felipesmz.bibliotecapessoal.dto.*;
import com.felipesmz.bibliotecapessoal.model.Livro;
import jakarta.validation.Valid;

public class LivroMapper {

    public LivroMapper() {
    }

    public static LivroResponse toResponse(Livro livro) {
        return new LivroResponse(
                livro.getId(),
                livro.getTitulo(),
                livro.getAutor(),
                livro.getGenero(),
                livro.getTotalPaginas(),
                livro.getPaginasLidas(),
                livro.getStatus().name(),
                livro.getAvaliacao(),
                livro.getDataCriacao(),
                livro.getDataAtualizacao()
        );
    }

    public static Livro toEntity(@Valid LivroCadastroRequest dto) {
        Livro livro = new Livro();
        livro.setTitulo(dto.getTitulo());
        livro.setAutor(dto.getAutor());
        livro.setGenero(dto.getGenero());
        livro.setTotalPaginas(dto.getTotalPaginas());
        livro.setStatus(dto.getStatus());
        return livro;
    }

    public static Livro toEntity(@Valid LivroAtualizarRequest dto) {
        Livro livroAtualizado = new Livro();
        livroAtualizado.setTitulo(dto.getTitulo());
        livroAtualizado.setAutor(dto.getAutor());
        livroAtualizado.setGenero(dto.getGenero());
        livroAtualizado.setTotalPaginas(dto.getTotalPaginas());

        return livroAtualizado;
    }

    public static Livro toEntity(@Valid LivroStatusPaginaRequest dto) {
        Livro livroAtualizado = new Livro();
        livroAtualizado.setPaginasLidas(dto.getPaginasLidas());

        return livroAtualizado;
    }

    public static Livro toEntity(@Valid LivroAvaliacaoRequest dto) {
        Livro livroAtualizado = new Livro();
        livroAtualizado.setAvaliacao(dto.getAvaliacao());

        return livroAtualizado;
    }
}


