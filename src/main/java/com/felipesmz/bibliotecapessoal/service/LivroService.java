package com.felipesmz.bibliotecapessoal.service;

import com.felipesmz.bibliotecapessoal.dto.LivroAtualizarRequest;
import com.felipesmz.bibliotecapessoal.dto.LivroAvaliacaoRequest;
import com.felipesmz.bibliotecapessoal.dto.LivroEstatisticaResponse;
import com.felipesmz.bibliotecapessoal.dto.LivroStatusPaginaRequest;
import com.felipesmz.bibliotecapessoal.model.Livro;
import com.felipesmz.bibliotecapessoal.model.Usuario;
import com.felipesmz.bibliotecapessoal.model.enums.Status;
import com.felipesmz.bibliotecapessoal.repository.LivroRepository;
import com.felipesmz.bibliotecapessoal.repository.UsuarioRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

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

    public Livro atualizarPaginasLidas(Long id, Long usuarioId, LivroStatusPaginaRequest dto) {

        Livro livroExistente = livroRepository.findByIdAndUsuarioId(id, usuarioId)
                .orElseThrow(() -> new RuntimeException("Livro não encontrado"));

        Integer paginasLidasAtual = livroExistente.getPaginasLidas();

        Integer paginasLidas = dto.getPaginasLidas();

        if (paginasLidasAtual > paginasLidas) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Você não pode diminuir o número de páginas lidas. O valor atual é " + paginasLidasAtual);
        }

        if (paginasLidas < 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Páginas lidas não pode ser negativa");
        }

        if (paginasLidas > livroExistente.getTotalPaginas()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Páginas lidas não pode ser maior que o total");
        }

        livroExistente.setPaginasLidas(paginasLidas);

        if (paginasLidas == 0) {
            livroExistente.setStatus(Status.QUERO_LER);

        } else if (paginasLidas.equals(livroExistente.getTotalPaginas())) {
            livroExistente.setStatus(Status.CONCLUIDO);

        } else {
            livroExistente.setStatus(Status.LENDO);
        }

        return livroRepository.save(livroExistente);
    }

    public Livro avaliarLivro(Long id, Long usuarioId, LivroAvaliacaoRequest dto) {

        Livro livroExistente = livroRepository.findByIdAndUsuarioId(id, usuarioId)
                .orElseThrow(() -> new RuntimeException("Livro não encontrado"));

        if (livroExistente.getStatus() != Status.CONCLUIDO) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Só é possível avaliar livros concluídos");
        }

        if (livroExistente.getAvaliacao() != null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Livro já foi avaliado");
        }

        livroExistente.setAvaliacao(dto.getAvaliacao());

        return livroRepository.save(livroExistente);

    }

    public LivroEstatisticaResponse obterStats(Long usuarioId) {

        Long total = livroRepository.countByUsuarioId(usuarioId);

        Long concluidos = livroRepository.countByUsuarioIdAndStatus(usuarioId, Status.CONCLUIDO);
        Long lendo = livroRepository.countByUsuarioIdAndStatus(usuarioId, Status.LENDO);
        Long queroLer = livroRepository.countByUsuarioIdAndStatus(usuarioId, Status.QUERO_LER);

        Double media = livroRepository.mediaAvaliacao(usuarioId);
        Integer paginas = livroRepository.somaPaginasLidas(usuarioId);

        return new LivroEstatisticaResponse(
                total,
                concluidos,
                lendo,
                queroLer,
                media != null ? media : 0.0,
                paginas != null ? paginas : 0
        );
    }
}