plugins {
    id("java")
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
    implementation("net.neoforged:neoforge:21.1.121")
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
