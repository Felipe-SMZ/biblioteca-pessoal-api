package com.felipesmz.bibliotecapessoal.controller;

import com.felipesmz.bibliotecapessoal.dto.LivroCadastroRequest;
import com.felipesmz.bibliotecapessoal.dto.LivroRequest;
import com.felipesmz.bibliotecapessoal.dto.LivroResponse;
import com.felipesmz.bibliotecapessoal.mapper.LivroMapper;
import com.felipesmz.bibliotecapessoal.model.Livro;
import com.felipesmz.bibliotecapessoal.security.JwtService;
import com.felipesmz.bibliotecapessoal.service.LivroService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/livros")
public class LivroController {

    private final JwtService jwtService;
    private LivroService livroService;

    public LivroController(LivroService livroService, JwtService jwtService) {
        this.livroService = livroService;
        this.jwtService = jwtService;
    }

    @PostMapping
    public ResponseEntity<LivroResponse> cadastroLivro(
            @Valid @RequestBody LivroCadastroRequest livroRequest,
            @RequestHeader("Authorization") String authHeader) {

        String token = authHeader.replace("Bearer ", "");
        Long usuarioId = jwtService.getUserIdFromToken(token);

        Livro livro = LivroMapper.toEntity(livroRequest);
        Livro livroSalvo = livroService.cadastroLivro(livro, usuarioId);
        LivroResponse response = LivroMapper.toResponse(livroSalvo);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}
