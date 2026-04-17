package com.felipesmz.bibliotecapessoal.mapper;

import com.felipesmz.bibliotecapessoal.dto.LivroRequest;
import com.felipesmz.bibliotecapessoal.dto.LivroResponse;
import com.felipesmz.bibliotecapessoal.model.Livro;

public class LivroMapper {

    public LivroMapper() {
    }

    public static Livro toEntity(LivroRequest dto) {
        Livro livro = new Livro();
        livro.setTitulo(dto.getTitulo());
        livro.setAutor(dto.getAutor());
        livro.setGenero(dto.getGenero());
        livro.setTotalPaginas(dto.getTotalPaginas());
        livro.setPaginasLidas(dto.getPaginasLidas());
        livro.setStatus(dto.getStatus());
        livro.setAvaliacao(dto.getAvaliacao());
        return livro;
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
                livro.getDataCriacao()
        );
    }
}
