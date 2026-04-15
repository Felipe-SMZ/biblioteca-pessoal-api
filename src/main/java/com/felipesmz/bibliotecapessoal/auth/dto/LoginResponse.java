package com.felipesmz.bibliotecapessoal.auth.dto;

import com.auth0.jwt.JWT;

public class LoginResponse {

    private String token;
    private String tipo;

    public LoginResponse(String token, String tipo) {
        this.token = token;
        this.tipo = tipo;
    }

    public String getToken() {
        return token;
    }

    public String getTipo() {
        return tipo;
    }
}
