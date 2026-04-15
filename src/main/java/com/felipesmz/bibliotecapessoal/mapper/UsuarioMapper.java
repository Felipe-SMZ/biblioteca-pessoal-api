package com.felipesmz.bibliotecapessoal.mapper;

import com.felipesmz.bibliotecapessoal.dto.UsuarioRequest;
import com.felipesmz.bibliotecapessoal.dto.UsuarioResponse;
import com.felipesmz.bibliotecapessoal.model.Usuario;

public class UsuarioMapper {

    private UsuarioMapper() {
    }

    public static Usuario toEntity(UsuarioRequest dto) {
        Usuario usuario = new Usuario();
        usuario.setNome(dto.getNome());
        usuario.setEmail(dto.getEmail());
        usuario.setSenha(dto.getSenha());
        return usuario;
    }

    public static UsuarioResponse toResponse(Usuario usuario) {
        return new UsuarioResponse(
                usuario.getId(),
                usuario.getNome(),
                usuario.getEmail(),
                usuario.getDataCriacao()
        );
    }

}