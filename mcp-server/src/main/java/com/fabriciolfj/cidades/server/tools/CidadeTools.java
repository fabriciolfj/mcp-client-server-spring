package com.fabriciolfj.cidades.server.tools;

import com.fabriciolfj.cidades.server.domain.CidadeRepository;
import org.springframework.ai.mcp.annotation.McpTool;
import org.springframework.ai.mcp.annotation.McpToolParam;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Tools MCP expostas pelo servidor.
 * Obs.: evite @Transactional nesta classe; o proxy pode esconder as anotações
 * do scanner. Os métodos do repositório já são transacionais.
 */
@Component
public class CidadeTools {

    private final CidadeRepository repository;

    public CidadeTools(CidadeRepository repository) {
        this.repository = repository;
    }

    @McpTool(name = "buscar_cidades_por_nome",
             description = "Busca cidades brasileiras cujo nome contenha o texto informado (case-insensitive)")
    public List<CidadeDto> buscarPorNome(
            @McpToolParam(description = "Trecho do nome da cidade, ex: 'Ribeir'", required = true) String nome) {
        return repository.findByNomeContainingIgnoreCase(nome).stream().map(CidadeDto::from).toList();
    }

    @McpTool(name = "buscar_cidades_por_uf",
             description = "Lista as cidades de um estado (UF), ordenadas por população decrescente")
    public List<CidadeDto> buscarPorUf(
            @McpToolParam(description = "Sigla do estado com 2 letras, ex: SP, RJ, MG", required = true) String uf) {
        if (uf == null || uf.length() != 2) {
            // RuntimeException -> vira CallToolResult com isError=true e chega ao modelo
            throw new IllegalArgumentException("UF inválida: " + uf);
        }
        return repository.findByUfIgnoreCaseOrderByPopulacaoDesc(uf).stream().map(CidadeDto::from).toList();
    }

    @McpTool(name = "maiores_cidades",
             description = "Retorna as N cidades mais populosas da base")
    public List<CidadeDto> maioresCidades(
            @McpToolParam(description = "Quantidade de cidades a retornar (1 a 50)", required = true) int limite) {
        int n = Math.max(1, Math.min(limite, 50));
        return repository.findAllByOrderByPopulacaoDesc(PageRequest.of(0, n)).stream().map(CidadeDto::from).toList();
    }
}
