package com.felipesmz.bibliotecapessoal.service;

import com.felipesmz.bibliotecapessoal.dto.LivroAtualizarRequest;
import com.felipesmz.bibliotecapessoal.model.Livro;
import com.felipesmz.bibliotecapessoal.model.Usuario;
import com.felipesmz.bibliotecapessoal.model.enums.Status;
import com.felipesmz.bibliotecapessoal.repository.LivroRepository;
import com.felipesmz.bibliotecapessoal.repository.UsuarioRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;

@Service
public class LivroService {

    private final UsuarioRepository usuarioRepository;
    LivroRepository livroRepository;

    public LivroService(LivroRepository livroRepository, UsuarioRepository usuarioRepository) {
        this.livroRepository = livroRepository;
        this.usuarioRepository = usuarioRepository;
    }


    public Livro cadastroLivro(Livro livro, Long usuarioId) {
        livro.setDataCriacao(LocalDateTime.now(ZoneOffset.UTC));
        if (livro.getStatus() == null) {
            livro.setStatus(Status.QUERO_LER);
        }
        livro.setPaginasLidas(0);
        Usuario usuario = usuarioRepository.getReferenceById(usuarioId);
        livro.setUsuario(usuario);

        return livroRepository.save(livro);
    }

    public List<Livro> todosLivros(Long usuarioId) {

        return livroRepository.findAllByUsuarioId(usuarioId);
    }

    public Livro buscaLivro(Long id, Long usuarioId) {

        return livroRepository.findByIdAndUsuarioId(id, usuarioId)
                .orElseThrow(() -> new RuntimeException("Livro não encontrado"));
    }

    public void deletarLivro(Long id, Long usuarioId) {
        if (livroRepository.findByIdAndUsuarioId(id, usuarioId).isPresent()) {
            livroRepository.deleteById(id);
        } else {
            throw new RuntimeException("Livro não encontrado");
        }
    }

    public Livro atualizarLivro(Long id, Long usuarioId, LivroAtualizarRequest dto) {

        Livro livroExistente = livroRepository.findByIdAndUsuarioId(id, usuarioId)
                .orElseThrow(() -> new RuntimeException("Livro não encontrado"));

        livroExistente.setTitulo(dto.getTitulo());
        livroExistente.setAutor(dto.getAutor());
        livroExistente.setGenero(dto.getGenero());
        livroExistente.setTotalPaginas(dto.getTotalPaginas());

        if (livroExistente.getPaginasLidas() != null) {

            if (livroExistente.getPaginasLidas() == 0) {
                livroExistente.setStatus(Status.QUERO_LER);

            } else if (livroExistente.getPaginasLidas().equals(livroExistente.getTotalPaginas())) {
                livroExistente.setStatus(Status.CONCLUIDO);

            } else {
                livroExistente.setStatus(Status.LENDO);
            }
        }

        // remove avaliação se não estiver concluído
        if (livroExistente.getStatus() != Status.CONCLUIDO) {
            livroExistente.setAvaliacao(null);
        }

        return livroRepository.save(livroExistente);
    }
}
