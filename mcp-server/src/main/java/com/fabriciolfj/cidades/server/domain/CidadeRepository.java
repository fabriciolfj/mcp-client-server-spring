package com.fabriciolfj.cidades.server.domain;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CidadeRepository extends JpaRepository<Cidade, Long> {

    List<Cidade> findByNomeContainingIgnoreCase(String nome);

    List<Cidade> findByUfIgnoreCaseOrderByPopulacaoDesc(String uf);

    List<Cidade> findAllByOrderByPopulacaoDesc(Pageable pageable);
}
