package com.fabriciolfj.cidades.server.tools;

import com.fabriciolfj.cidades.server.domain.Cidade;

public record CidadeDto(Long id, String nome, String uf, Long populacao) {
    static CidadeDto from(Cidade c) {
        return new CidadeDto(c.getId(), c.getNome(), c.getUf(), c.getPopulacao());
    }
}
