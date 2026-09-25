plugins {
    java
    id("org.springframework.boot") version "4.1.1" apply false
    id("io.spring.dependency-management") version "1.1.7" apply false
}

val springAiVersion by extra("2.0.1")

subprojects {
    apply(plugin = "java")
    apply(plugin = "org.springframework.boot")
    apply(plugin = "io.spring.dependency-management")

    group = "com.fabriciolfj"
    version = "0.0.1-SNAPSHOT"

    java { toolchain { languageVersion.set(JavaLanguageVersion.of(25)) } }

    repositories { mavenCentral() }

    the<io.spring.gradle.dependencymanagement.dsl.DependencyManagementExtension>().apply {
        imports { mavenBom("org.springframework.ai:spring-ai-bom:${rootProject.extra["springAiVersion"]}") }
    }

    tasks.withType<JavaCompile> { options.compilerArgs.add("-parameters") }
    tasks.withType<Test> { useJUnitPlatform() }
}
