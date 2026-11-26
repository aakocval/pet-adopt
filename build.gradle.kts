// Root build: declare plugins once (apply false) so every module shares the same versions.
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.compose) apply false
    alias(libs.plugins.ksp) apply false
    alias(libs.plugins.hilt) apply false
    alias(libs.plugins.ktlint)
}

// Formatting is checked in every project (including build scripts). Run `./gradlew ktlintFormat` to fix.
allprojects {
    apply(plugin = "org.jlleitschuh.gradle.ktlint")
    configure<org.jlleitschuh.gradle.ktlint.KtlintExtension> {
        version.set(rootProject.libs.versions.ktlintCli)
        filter { exclude { it.file.path.contains("/build/") } }
    }
}
