package com.felipesmz.bibliotecapessoal.security;

import com.felipesmz.bibliotecapessoal.model.Usuario;
import com.felipesmz.bibliotecapessoal.repository.UsuarioRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;

public class CustomUserDetailsService implements UserDetailsService {

    private final UsuarioRepository usuarioRepository;

    public CustomUserDetailsService(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String email) {
        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado: " + email));
        return org.springframework.security.core.userdetails.User
                .withUsername(usuario.getEmail())
                .password(usuario.getSenha())
                .roles("ROLE_USER")
                .build();
    }
}