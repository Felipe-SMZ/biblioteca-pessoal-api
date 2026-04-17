package com.felipesmz.bibliotecapessoal.dto;

import java.time.LocalDateTime;
import java.util.Map;

public class ErrorResponse {

    private LocalDateTime timestamp;
    private int status;
    private String error;
    private String mensagem;
    private Map<String, String> erros;

    public ErrorResponse(LocalDateTime timestamp, int status, String error, String mensagem, Map<String, String> erros) {
        this.timestamp = timestamp;
        this.status = status;
        this.error = error;
        this.mensagem = mensagem;
        this.erros = erros;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public int getStatus() {
        return status;
    }

    public String getError() {
        return error;
    }

    public String getMensagem() {
        return mensagem;
    }

    public Map<String, String> getErros() {
        return erros;
    }
}