plugins {
    id 'java'
}

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
}

repositories {
    mavenCentral()
    maven {
        name = "NeoForge"
        url = "https://maven.neoforged.net/releases"
    }
}

dependencies {
    minecraft "net.neoforged:neoforge:21.1.121"
}

minecraft {
    // Configuration spécifique à NeoForge ModDev
}
