package com.felipesmz.bibliotecapessoal.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class UsuarioRequest {

    @NotBlank(message = "O nome não pode ser vazio")
    @Size(min = 2, max = 100, message = "O nome deve conter entre 2 e 100 caracteres")
    private String nome;

    @NotBlank(message = "O email não pode ser em branco")
    @Email(message = "O email deve ser válido")
    @Size(min = 5, max = 100, message = "O email deve conter entre 5 e 100 caracteres")
    private String email;

    @NotBlank(message = "A senha não pode ser em branco")
    @Size(min = 6, max = 20, message = "A senha deve conter entre 6 e 20 caracteres" )
    private String senha;

    public UsuarioRequest() {}

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getSenha() {
        return senha;
    }

    public void setSenha(String senha) {
        this.senha = senha;
    }
}