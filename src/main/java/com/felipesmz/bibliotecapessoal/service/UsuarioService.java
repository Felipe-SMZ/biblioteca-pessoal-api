package com.felipesmz.bibliotecapessoal.service;

import com.felipesmz.bibliotecapessoal.model.Usuario;
import com.felipesmz.bibliotecapessoal.repository.UsuarioRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.Objects;

@Service
public class UsuarioService {

    //injeção de dependências
    private final UsuarioRepository usuarioRepository;

    public UsuarioService(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    //regras de negocio
    public Usuario buscarOuFalhar(Long id) {
        return usuarioRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Usuário " + id + " não encontrado"));
    }

    public void validarEmail(Long id, Usuario usuario) {
        usuarioRepository.findByEmail(usuario.getEmail())
                .ifPresent(usuarioExistente -> {
                    if (!Objects.equals(usuarioExistente.getId(), id)) {
                        throw new ResponseStatusException(
                                HttpStatus.CONFLICT, "E-mail já cadastrado");
                    }
                });
    }
}