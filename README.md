# ShopAndEconomy - ready build package

This package contains a corrected `build.gradle.kts` (Kotlin DSL) and a GitHub Actions workflow configured to build with Java 21 for NeoForge 1.21.1.

Important notes:
- It's best to commit the Gradle Wrapper (`gradlew`, `gradlew.bat` and `gradle/wrapper/*`) into the repo.
- If you don't include the wrapper, the workflow will attempt to install Gradle via sdkman on the runner.
- Before pushing, you can generate the wrapper locally (recommended) with:
  - `./gradlew wrapper` (on Linux/macOS) or `gradle wrapper` if gradle is installed.
  - Commit `gradlew`, `gradlew.bat` and `gradle/wrapper/gradle-wrapper.jar` and `gradle/wrapper/gradle-wrapper.properties`.

How to use:
1. Add your mod source under `src/main/java` and resources under `src/main/resources`.
2. Commit & push to GitHub `main` branch.
3. Go to Actions → watch the build. Download artifact `mod-jar` when finished.

If you want, I can now:
- add the complete mod source into this package (commands, shop, etc.), or
- attempt to generate the Gradle wrapper files here (might be limited).
