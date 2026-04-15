package com.felipesmz.bibliotecapessoal.service;

import com.felipesmz.bibliotecapessoal.model.Usuario;
import com.felipesmz.bibliotecapessoal.repository.UsuarioRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.server.ResponseStatusException;

import java.util.Objects;
import java.util.Optional;

@Service
public class UsuarioService {

    //injeção de dependências
    private final UsuarioRepository usuarioRepository;

    public UsuarioService(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    //regras de negocio
    private Usuario buscarOuFalhar(Long id) {
        return usuarioRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Usuário " + id + " não encontrado"));
    }

    private void validarEmail(Long id, Usuario usuario) {
        usuarioRepository.findByEmail(usuario.getEmail())
                .ifPresent(usuarioExistente -> {
                    if (!Objects.equals(usuarioExistente.getId(), id)) {
                        throw new ResponseStatusException(
                                HttpStatus.CONFLICT, "E-mail já cadastrado");
                    }
                });
    }

    public Usuario salvar(Usuario usuario) {
        validarEmail(null, usuario);
        return usuarioRepository.save(usuario);
    }

    public Usuario buscarPorId(Long id) {
        return buscarOuFalhar(id);
    }

    public Usuario atualizar(Long id, Usuario usuarioAtualizado) {
        Usuario usuarioExistente = buscarOuFalhar(id);
        validarEmail(id, usuarioAtualizado);
        usuarioExistente.setNome(usuarioAtualizado.getNome());
        usuarioExistente.setEmail(usuarioAtualizado.getEmail());
        usuarioExistente.setSenha(usuarioAtualizado.getSenha());

        return usuarioRepository.save(usuarioExistente);
    }
}