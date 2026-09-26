package com.fabriciolfj.cidades.client.web;

import io.modelcontextprotocol.client.McpSyncClient;
import io.modelcontextprotocol.spec.McpSchema;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Objects;


@RestController
@RequestMapping("/mcp")
public class McpDiretoController {

    private final McpSyncClient cidades;
    private final McpSyncClient populacao;

    public McpDiretoController(List<McpSyncClient> clients) {
        // duas conexões configuradas: seleciona cada client pelo nome informado pelo servidor
        this.cidades = porServidor(clients, "cidades-mcp-server");
        this.populacao = porServidor(clients, "populacao-mcp-server");
    }

    @GetMapping("/tools")
    public Map<String, List<Map<String, Object>>> tools() {
        return Map.of(
                "cidades-mcp-server", toolsDe(cidades),
                "populacao-mcp-server", toolsDe(populacao));
    }

    @GetMapping("/cidades")
    public String buscarPorNome(@RequestParam String nome) {
        return chamar(cidades, "buscar_cidades_por_nome", Map.of("nome", nome));
    }

    @GetMapping("/cidades/uf/{uf}")
    public String buscarPorUf(@PathVariable String uf) {
        return chamar(cidades, "buscar_cidades_por_uf", Map.of("uf", uf));
    }

    @GetMapping("/cidades/maiores")
    public String maiores(@RequestParam(defaultValue = "5") int limite) {
        return chamar(cidades, "maiores_cidades", Map.of("limite", limite));
    }

    @GetMapping("/populacao/{cidade}")
    public String consultarPopulacao(@PathVariable String cidade) {
        return chamar(populacao, "consultar_populacao", Map.of("cidade", cidade));
    }

    @PostMapping("/populacao/{cidade}/incrementar")
    public String incrementarPopulacao(@PathVariable String cidade, @RequestParam long quantidade) {
        return chamar(populacao, "incrementar_populacao", Map.of("cidade", cidade, "quantidade", quantidade));
    }

    private static McpSyncClient porServidor(List<McpSyncClient> clients, String nomeServidor) {
        return clients.stream()
                .filter(c -> nomeServidor.equals(c.getServerInfo().name()))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("MCP server não conectado: " + nomeServidor));
    }

    private List<Map<String, Object>> toolsDe(McpSyncClient client) {
        return client.listTools().tools().stream()
                .map(t -> Map.of(
                        "name", t.name(),
                        "description", Objects.requireNonNullElse(t.description(), ""),
                        "inputSchema", t.inputSchema()))
                .toList();
    }

    private String chamar(McpSyncClient client, String tool, Map<String, Object> args) {
        var request = McpSchema.CallToolRequest.builder(tool)
                .arguments(args)
                .build();

        var result = client.callTool(request);

        if (Boolean.TRUE.equals(result.isError())) {
            throw new IllegalStateException("Erro na tool MCP '%s': %s".formatted(tool, textoDe(result)));
        }
        return textoDe(result);
    }

    private String textoDe(McpSchema.CallToolResult result) {
        StringBuilder sb = new StringBuilder();
        for (McpSchema.Content c : result.content()) {
            // Content não é mais sealed no SDK 2.0 -> switch precisa de default
            switch (c) {
                case McpSchema.TextContent tc -> sb.append(tc.text());
                default -> sb.append("[conteúdo não textual: ").append(c.type()).append(']');
            }
        }
        return sb.toString();
    }
}
