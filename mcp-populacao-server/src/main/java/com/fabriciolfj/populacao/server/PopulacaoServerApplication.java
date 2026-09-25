package com.fabriciolfj.populacao.server;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Segundo MCP server do exemplo: mantém a população das cidades em memória
 * e expõe tools (@McpTool) para consultar e incrementar esses valores.
 */
@SpringBootApplication
public class PopulacaoServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(PopulacaoServerApplication.class, args);
    }
}
