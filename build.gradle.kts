plugins {
    `java`
}

val neoforgeVersion: String by project

repositories {
    maven { url = uri("https://maven.neoforged.net/releases/") }
    mavenCentral()
}

dependencies {
    implementation("net.neoforged:neoforge:${neoforgeVersion}")
    implementation("com.google.code.gson:gson:2.10.1")
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(17))
    }
}

tasks.register<Copy>("prepareJar") {
    dependsOn("jar")
}
