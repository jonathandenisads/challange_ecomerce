package com.ecommerce.infrastructure.search;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
public class ProductIndex {

    @JsonProperty("id")
    private UUID id;

    @JsonProperty("nome")
    private String nome;

    @JsonProperty("descricao")
    private String descricao;

    @JsonProperty("preco")
    private BigDecimal preco;

    @JsonProperty("categoria")
    private String categoria;

    @JsonProperty("quantidadeEstoque")
    private Integer quantidadeEstoque;

    @JsonProperty("dataCriacao")
    private LocalDateTime dataCriacao;

    @JsonProperty("dataAtualizacao")
    private LocalDateTime dataAtualizacao;

    public ProductIndex() {
    }

    public ProductIndex(UUID id, String nome, String descricao, BigDecimal preco, String categoria,
                        Integer quantidadeEstoque, LocalDateTime dataCriacao, LocalDateTime dataAtualizacao) {
        this.id = id;
        this.nome = nome;
        this.descricao = descricao;
        this.preco = preco;
        this.categoria = categoria;
        this.quantidadeEstoque = quantidadeEstoque;
        this.dataCriacao = dataCriacao;
        this.dataAtualizacao = dataAtualizacao;
    }
}

    