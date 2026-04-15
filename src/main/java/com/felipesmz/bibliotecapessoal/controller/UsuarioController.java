package com.felipesmz.bibliotecapessoal.controller;

import com.felipesmz.bibliotecapessoal.dto.UsuarioRequest;
import com.felipesmz.bibliotecapessoal.dto.UsuarioResponse;
import com.felipesmz.bibliotecapessoal.mapper.UsuarioMapper;
import com.felipesmz.bibliotecapessoal.model.Usuario;
import com.felipesmz.bibliotecapessoal.service.UsuarioService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/usuarios")
public class UsuarioController {

    private final UsuarioService usuarioService;

    public UsuarioController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @PostMapping
    public ResponseEntity<UsuarioResponse> cadastrarUsuario(@Valid @RequestBody UsuarioRequest usuarioRequest) {

        Usuario usuario = UsuarioMapper.toEntity(usuarioRequest);
        Usuario usuarioSalvo = usuarioService.salvar(usuario);
        UsuarioResponse response = UsuarioMapper.toResponse(usuarioSalvo);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<UsuarioResponse> buscarUsuarioPorId(@PathVariable Long id) {
        Usuario usuario = usuarioService.buscarPorId(id);
        UsuarioResponse response = UsuarioMapper.toResponse(usuario);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<UsuarioResponse> atualizarUsuarioPorId(@PathVariable Long id, @Valid @RequestBody UsuarioRequest usuarioRequest) {
        Usuario usuario = UsuarioMapper.toEntity(usuarioRequest);
        Usuario usuarioAtualizado = usuarioService.atualizar(id, usuario);
        UsuarioResponse response = UsuarioMapper.toResponse(usuarioAtualizado);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> removerUsuarioPorId(@PathVariable Long id) {
        usuarioService.deletar(id);
        return ResponseEntity.noContent().build();
    }

}
