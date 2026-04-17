package com.felipesmz.bibliotecapessoal.dto;

public class LivroEstatisticaResponse {

    private Long totalLivros;
    private Long concluidos;
    private Long lendo;
    private Long queroLer;
    private Double mediaAvaliacao;
    private Integer totalPaginasLidas;

    public LivroEstatisticaResponse(Long totalLivros, Long concluidos, Long lendo,
                                    Long queroLer, Double mediaAvaliacao, Integer totalPaginasLidas) {
        this.totalLivros = totalLivros;
        this.concluidos = concluidos;
        this.lendo = lendo;
        this.queroLer = queroLer;
        this.mediaAvaliacao = mediaAvaliacao;
        this.totalPaginasLidas = totalPaginasLidas;
    }

    public Long getTotalLivros() {
        return totalLivros;
    }

    public Long getConcluidos() {
        return concluidos;
    }

    public Long getLendo() {
        return lendo;
    }

    public Long getQueroLer() {
        return queroLer;
    }

    public Double getMediaAvaliacao() {
        return mediaAvaliacao;
    }

    public Integer getTotalPaginasLidas() {
        return totalPaginasLidas;
    }
}