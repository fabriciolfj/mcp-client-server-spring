package com.fabriciolfj.cidades.client;

import com.fabriciolfj.cidades.client.tools.SotaqueTools;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class McpClientApplication {

    public static void main(String[] args) {
        SpringApplication.run(McpClientApplication.class, args);
    }

    /**
     * O starter do MCP client conecta nos DOIS servidores remotos (cidades e populacao),
     * faz tools/list em cada um e expõe todas as tools num único ToolCallbackProvider.
     * Além delas, o modelo recebe as tools LOCAIS de SotaqueTools (@Tool).
     * Spring AI 2.x: defaultTools(...) aceita tanto ToolCallbackProvider quanto objetos
     * com métodos @Tool, e o ToolCallingAdvisor é registrado automaticamente.
     */
    @Bean
    ChatClient chatClient(ChatClient.Builder builder, ToolCallbackProvider mcpTools, SotaqueTools sotaqueTools) {
        return builder
                .defaultSystem("""
                        Você é um assistente que responde perguntas sobre cidades brasileiras.
                        Use SEMPRE as tools disponíveis; não invente dados:
                        - cidades (nome, UF, ranking): tools do servidor de cidades;
                        - consultar ou incrementar população: tools do servidor de população;
                        - sotaques por estado: tools locais de sotaque.
                        Responda em português.""")
                .defaultTools(mcpTools, sotaqueTools)
                .build();
    }
}
