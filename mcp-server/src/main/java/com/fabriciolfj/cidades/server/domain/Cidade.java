package com.fabriciolfj.cidades.server.domain;

import jakarta.persistence.*;

@Entity
@Table(name = "cidade")
public class Cidade {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String nome;
    private String uf;
    private Long populacao;

    protected Cidade() {}

    public Long getId() { return id; }
    public String getNome() { return nome; }
    public String getUf() { return uf; }
    public Long getPopulacao() { return populacao; }
}
