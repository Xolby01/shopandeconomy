plugins {
    id("java")
    id("net.neoforged.gradle") version "6.0.20" // plugin officiel NeoForge Gradle
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(21))
    }
}

repositories {
    mavenCentral()
    maven {
        name = "NeoForge"
        url = uri("https://maven.neoforged.net/releases")
    }
}

dependencies {
    minecraft("net.neoforged:neoforge:21.1.121") // et pas implementation()
    implementation("com.google.code.gson:gson:2.10.1")
}

tasks.jar {
    archiveBaseName.set("ShopAndEconomy")
    archiveVersion.set("1.0.0")
    manifest {
        attributes["ModId"] = "shopandeconomy"
        attributes["Implementation-Title"] = "Shop and Economy Mod"
        attributes["Implementation-Version"] = archiveVersion.get()
    }
}
