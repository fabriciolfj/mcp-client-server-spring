# mcp-cidades

Exemplo com três apps Spring AI (Gradle multi-project, Kotlin DSL): um client usando
**dois MCP servers** + uma **tool local** (`@Tool`).

| Módulo                 | Porta | Papel                                                                  |
|------------------------|-------|------------------------------------------------------------------------|
| `mcp-server`           | 8081  | MCP Server de cidades (Streamable HTTP em `/mcp`), tools sobre H2/JPA  |
| `mcp-populacao-server` | 8082  | MCP Server de população (Streamable HTTP em `/mcp`), dados em memória  |
| `mcp-client`           | 8080  | MCP Client (conecta nos 2 servers) + ChatClient + `@Tool` de sotaques  |

**Stack:** Java 25 · Spring Boot 4.1.1 · Spring AI 2.0.1 (MCP Java SDK 2.0) · Gradle 9

## Tools disponíveis para o LLM

| Origem                             | Tipo         | Tools                                                                  |
|------------------------------------|--------------|------------------------------------------------------------------------|
| `mcp-server` (remota)              | `@McpTool`   | `buscar_cidades_por_nome(nome)`, `buscar_cidades_por_uf(uf)`, `maiores_cidades(limite)` |
| `mcp-populacao-server` (remota)    | `@McpTool`   | `consultar_populacao(cidade)`, `incrementar_populacao(cidade, quantidade)` |
| `mcp-client` (local, `SotaqueTools`) | `@Tool`    | `listar_sotaques()`, `sotaques_por_uf(uf)` (lista fixa, sem banco)     |

O `ChatClient` recebe as duas fontes em `defaultTools(mcpTools, sotaqueTools)`: o
`ToolCallbackProvider` do starter agrega as tools de todas as conexões MCP.

## Rodando

```bash
gradle wrapper --gradle-version 9.1.0   # uma vez, se não tiver o wrapper

./gradlew :mcp-server:bootRun           # terminal 1
./gradlew :mcp-populacao-server:bootRun # terminal 2

export ANTHROPIC_API_KEY=sk-...
./gradlew :mcp-client:bootRun           # terminal 3
```

## Testando

Sem LLM (chamada direta ao MCP):
```bash
curl localhost:8080/mcp/tools
curl "localhost:8080/mcp/cidades?nome=ribeir"
curl localhost:8080/mcp/cidades/uf/SP
curl "localhost:8080/mcp/cidades/maiores?limite=3"
curl "localhost:8080/mcp/populacao/Curitiba"
curl -X POST "localhost:8080/mcp/populacao/Curitiba/incrementar?quantidade=500"
```

Com LLM:
```bash
curl -G localhost:8080/chat --data-urlencode "q=Quais as 3 maiores cidades de SP?"

# usa os 2 MCP servers + a tool local numa única pergunta
curl -G localhost:8080/chat --data-urlencode \
  "q=Qual a maior cidade do RS? Some 1000 habitantes à população dela e me diga o sotaque falado lá."
```

MCP Inspector:
```bash
npx @modelcontextprotocol/inspector
# Transport: Streamable HTTP | URL: http://localhost:8081/mcp (ou :8082/mcp)
```

## Mudanças relevantes em relação ao Spring AI 1.1
- `@McpTool`/`@McpToolParam` agora em `org.springframework.ai.mcp.annotation`
  (antes `org.springaicommunity.mcp.annotation`); registro automático via annotation scanner.
- Streamable HTTP é o protocolo padrão; SSE está deprecated.
- `ChatClient.defaultTools(...)` aceita `ToolCallbackProvider`; `defaultToolCallbacks` deprecated.
- `ToolCallingAdvisor` é auto-registrado; o loop de tools saiu dos `ChatModel`.
- MCP SDK 2.0: `CallToolRequest.builder(name)`, `Tool.inputSchema()` é `Map`,
  `Content` não é mais sealed, validação de input das tools ligada por padrão.
- Customização do client: `McpClientCustomizer<B>` (substitui `McpSyncClientCustomizer`).
- Servidor HTTP não tem autenticação por padrão: proteja com Spring Security / mcp-security
  antes de expor fora de localhost.
