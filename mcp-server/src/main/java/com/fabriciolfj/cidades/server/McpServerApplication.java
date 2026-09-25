package com.fabriciolfj.cidades.server;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Spring AI 2.x: os métodos anotados com @McpTool são descobertos pelo
 * annotation scanner e registrados automaticamente no MCP server.
 * Não é mais necessário declarar um ToolCallbackProvider manualmente.
 */
@SpringBootApplication
public class McpServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(McpServerApplication.class, args);
    }
}
