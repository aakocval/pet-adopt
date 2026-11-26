// Root build: declare plugins once (apply false) so every module shares the same versions.
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.compose) apply false
    alias(libs.plugins.kotlin.serialization) apply false
    alias(libs.plugins.ksp) apply false
    alias(libs.plugins.hilt) apply false
    alias(libs.plugins.ktlint)
    alias(libs.plugins.detekt)
}

// Formatting is checked in every project (including build scripts). Run `./gradlew ktlintFormat` to fix.
allprojects {
    apply(plugin = "org.jlleitschuh.gradle.ktlint")
    configure<org.jlleitschuh.gradle.ktlint.KtlintExtension> {
        version.set(rootProject.libs.versions.ktlintCli)
        filter { exclude { it.file.path.contains("/build/") } }
    }
}

// Static analysis. `./gradlew detekt` scans every module's Kotlin sources.
detekt {
    buildUponDefaultConfig = true
    allRules = false
    parallel = true
    config.setFrom(files("config/detekt/detekt.yml"))
    source.setFrom(
        fileTree(rootDir) {
            include("**/src/**/kotlin/**/*.kt", "**/*.kts")
            exclude("**/build/**")
        },
    )
}

// detekt 1.23 is compiled against Kotlin 2.0.21. Keep its own classpath on that version
// so the project's newer Kotlin plugin doesn't leak in. Drop this when moving to detekt 2.x.
configurations.matching { it.name == "detekt" }.configureEach {
    resolutionStrategy.eachDependency {
        if (requested.group == "org.jetbrains.kotlin") useVersion("2.0.21")
    }
}

tasks.withType<io.gitlab.arturbosch.detekt.Detekt>().configureEach {
    jvmTarget = "17"
    reports {
        html.required.set(true)
        sarif.required.set(true) // uploaded to GitHub code scanning in CI
        xml.required.set(false)
        txt.required.set(false)
    }
}
