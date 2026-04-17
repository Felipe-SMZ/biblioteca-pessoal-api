package com.felipesmz.bibliotecapessoal.service;

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
}
