// Root build: declare plugins once (apply false) so every module shares the same versions.
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.compose) apply false
}
