package com.felipesmz.bibliotecapessoal.controller;

import com.felipesmz.bibliotecapessoal.dto.LivroCadastroRequest;
import com.felipesmz.bibliotecapessoal.dto.LivroResponse;
import com.felipesmz.bibliotecapessoal.mapper.LivroMapper;
import com.felipesmz.bibliotecapessoal.model.Livro;
import com.felipesmz.bibliotecapessoal.repository.UsuarioRepository;
import com.felipesmz.bibliotecapessoal.security.JwtService;
import com.felipesmz.bibliotecapessoal.service.LivroService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/livros")
public class LivroController {

    private final JwtService jwtService;
    private final LivroService livroService;
    private final UsuarioRepository usuarioRepository;

    public LivroController(LivroService livroService, JwtService jwtService, UsuarioRepository usuarioRepository) {
        this.livroService = livroService;
        this.jwtService = jwtService;
        this.usuarioRepository = usuarioRepository;

    }

    private Long getUsuarioIdAutenticado() {
        String email = ((UserDetails) SecurityContextHolder.getContext()
                .getAuthentication().getPrincipal()).getUsername();

        // .orElseThrow garante que se o usuário não for encontrado, uma exceção seja lançada
        return usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"))
                .getId();
    }

    @PostMapping
    public ResponseEntity<LivroResponse> cadastroLivro(
            @Valid @RequestBody LivroCadastroRequest livroRequest) {

        Long usuarioId = getUsuarioIdAutenticado();

        Livro livro = LivroMapper.toEntity(livroRequest);
        Livro livroSalvo = livroService.cadastroLivro(livro, usuarioId);
        LivroResponse response = LivroMapper.toResponse(livroSalvo);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    public ResponseEntity<List<LivroResponse>> listarLivros() {

        Long usuarioId = getUsuarioIdAutenticado();

        List<Livro> livros = livroService.todosLivros(usuarioId);

        List<LivroResponse> response = livros.stream()
                .map(LivroMapper::toResponse)
                .toList();

        return ResponseEntity.status(HttpStatus.OK).body(response);

    }
}
