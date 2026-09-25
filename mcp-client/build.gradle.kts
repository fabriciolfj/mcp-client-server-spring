dependencies {
    implementation("org.springframework.boot:spring-boot-starter-webmvc")
    implementation("org.springframework.ai:spring-ai-starter-mcp-client")
    // Modelo de chat (troque por spring-ai-starter-model-openai / ollama se preferir)
    implementation("org.springframework.ai:spring-ai-starter-model-anthropic")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
}
