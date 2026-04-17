package com.felipesmz.bibliotecapessoal.service;

import com.felipesmz.bibliotecapessoal.model.Usuario;
import com.felipesmz.bibliotecapessoal.repository.UsuarioRepository;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Objects;

@Service
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    public UsuarioService(UsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
    }

    private Usuario buscarOuFalhar(Long id) {
        return usuarioRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Usuário " + id + " não encontrado"));
    }

    private void validarEmail(Long id, Usuario usuario) {
        // Em update, permite manter o proprio email; bloqueia email ja usado por outro usuario.
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
        String senhaCriptografada = passwordEncoder.encode(usuario.getSenha());
        usuario.setSenha(senhaCriptografada);
        usuario.setDataCriacao(LocalDateTime.now(ZoneOffset.UTC));
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
        // So troca a senha quando ela vier preenchida no request.
        if (usuarioAtualizado.getSenha() != null && !usuarioAtualizado.getSenha().isBlank()) {
            String senhaCriptografada = passwordEncoder.encode(usuarioAtualizado.getSenha());
            usuarioExistente.setSenha(senhaCriptografada);
        }

        return usuarioRepository.save(usuarioExistente);
    }

    public void deletar(Long id) {
        buscarOuFalhar(id);
        usuarioRepository.deleteById(id);
    }
}