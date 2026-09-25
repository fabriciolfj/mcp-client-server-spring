package com.fabriciolfj.populacao.server.tools;

import org.springframework.ai.mcp.annotation.McpTool;
import org.springframework.ai.mcp.annotation.McpToolParam;
import org.springframework.stereotype.Component;

import java.text.Normalizer;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Tools MCP de exemplo sobre um "contador" de população em memória.
 * Os dados somem ao reiniciar o servidor (não há banco aqui de propósito).
 */
@Component
public class PopulacaoTools {

    /** chave normalizada (sem acento, minúscula) -> população */
    private final Map<String, PopulacaoDto> populacoes = new ConcurrentHashMap<>();

    public PopulacaoTools() {
        registrar("São Paulo", 11_451_999);
        registrar("Rio de Janeiro", 6_211_223);
        registrar("Brasília", 2_817_381);
        registrar("Salvador", 2_418_005);
        registrar("Belo Horizonte", 2_315_560);
        registrar("Curitiba", 1_773_718);
        registrar("Porto Alegre", 1_332_845);
        registrar("Ribeirão Preto", 698_642);
    }

    @McpTool(name = "consultar_populacao",
             description = "Retorna a população atual (contador em memória) de uma cidade pelo nome exato")
    public PopulacaoDto consultar(
            @McpToolParam(description = "Nome da cidade, ex: 'Ribeirão Preto'", required = true) String cidade) {
        var atual = populacoes.get(chave(cidade));
        if (atual == null) {
            throw new IllegalArgumentException("Cidade não encontrada: " + cidade);
        }
        return atual;
    }

    @McpTool(name = "incrementar_populacao",
             description = "Soma a quantidade informada à população da cidade e retorna o novo total. "
                     + "Se a cidade não existir, ela é criada com a quantidade informada.")
    public PopulacaoDto incrementar(
            @McpToolParam(description = "Nome da cidade, ex: 'Curitiba'", required = true) String cidade,
            @McpToolParam(description = "Quantidade de habitantes a somar (maior que zero)", required = true) long quantidade) {
        if (quantidade <= 0) {
            // RuntimeException -> vira CallToolResult com isError=true e chega ao modelo
            throw new IllegalArgumentException("Quantidade deve ser maior que zero: " + quantidade);
        }
        return populacoes.merge(chave(cidade), new PopulacaoDto(cidade, quantidade),
                (atual, novo) -> new PopulacaoDto(atual.cidade(), atual.populacao() + novo.populacao()));
    }

    private void registrar(String cidade, long populacao) {
        populacoes.put(chave(cidade), new PopulacaoDto(cidade, populacao));
    }

    private static String chave(String cidade) {
        if (cidade == null || cidade.isBlank()) {
            throw new IllegalArgumentException("Nome da cidade é obrigatório");
        }
        return Normalizer.normalize(cidade.trim(), Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "")
                .toLowerCase(Locale.ROOT);
    }
}
