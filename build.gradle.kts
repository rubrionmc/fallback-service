plugins {
    java
    `maven-publish`
    id("io.freefair.lombok") version "9.2.0"
}

group = "net.rubrion.server"
version = "1.0"

repositories {
    mavenCentral()
    maven("https://leycm.github.io/repository/")
}

dependencies {
    implementation("org.slf4j:slf4j-simple:2.0.17")
    implementation("de.leycm:i18label4j-api:1.0")
    implementation("de.leycm:i18label4j-impl:1.0")
    implementation("de.leycm:init4j-api:1.3")
    implementation("net.minestom:minestom:2026.01.08-1.21.11")
    implementation("com.google.code.gson:gson:2.13.2")
    implementation("net.kyori:adventure-text-minimessage:4.17.0")
    implementation("info.picocli:picocli:4.7.6")
}

configurations.all {
    resolutionStrategy {
        force("com.google.code.gson:gson:2.13.2")
    }
}

tasks.test {
    useJUnitPlatform()
}

tasks.jar {
    archiveBaseName.set("fallback")
    archiveVersion.set(version.toString())
    manifest {
        attributes["Main-Class"] = "net.rubrion.server.fallback.FallbackPodCommand"
    }

    from({
        configurations.runtimeClasspath.get()
            .filter { it.name.endsWith("jar") }
            .map { zipTree(it) }
    })

    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
}