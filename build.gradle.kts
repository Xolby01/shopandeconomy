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
    implementation("net.neoforged:neoforge:${neo_version}")
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
sourceSets {
  main {
    java { exclude 'com/example/examplemod/**' }
    resources {
      exclude 'assets/examplemod/**'
      exclude 'META-INF/mods.toml' // si c'était l’ancien
    }
  }
}
