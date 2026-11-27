import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.ksp)
    alias(libs.plugins.hilt)
}

/**
 * Release signing comes from the environment (CI secrets) or an untracked keystore.properties
 * (local). Neither is in the repo. Without them the release build is simply unsigned.
 */
data class ReleaseSigning(
    val storeFile: String,
    val storePassword: String,
    val keyAlias: String,
    val keyPassword: String,
)

fun releaseSigningFromEnv(): ReleaseSigning? {
    val env = { name: String -> providers.environmentVariable(name).orNull }
    val storeFile = env("PETADOPT_KEYSTORE_FILE") ?: return null
    return ReleaseSigning(
        storeFile = storeFile,
        storePassword = requireNotNull(env("PETADOPT_KEYSTORE_PASSWORD")) { "PETADOPT_KEYSTORE_PASSWORD missing" },
        keyAlias = requireNotNull(env("PETADOPT_KEY_ALIAS")) { "PETADOPT_KEY_ALIAS missing" },
        keyPassword = requireNotNull(env("PETADOPT_KEY_PASSWORD")) { "PETADOPT_KEY_PASSWORD missing" },
    )
}

fun releaseSigningFromFile(): ReleaseSigning? {
    val file = rootProject.file("keystore.properties").takeIf { it.exists() } ?: return null
    val props = Properties().apply { file.inputStream().use(::load) }
    return ReleaseSigning(
        storeFile = props.getProperty("storeFile"),
        storePassword = props.getProperty("storePassword"),
        keyAlias = props.getProperty("keyAlias"),
        keyPassword = props.getProperty("keyPassword"),
    )
}

android {
    namespace = "io.github.aakocval.petadopt"
    compileSdk {
        // Compose BOM 2026.11 needs the 37.1+ SDK extension; targetSdk stays on the major level.
        version = release(37) { minorApiLevel = 2 }
    }

    defaultConfig {
        applicationId = "io.github.aakocval.petadopt"
        minSdk = 26
        targetSdk = 37
        versionCode = 8
        versionName = "0.8.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    val signing = releaseSigningFromEnv() ?: releaseSigningFromFile()
    signingConfigs {
        if (signing != null) {
            create("release") {
                storeFile = file(signing.storeFile)
                storePassword = signing.storePassword
                keyAlias = signing.keyAlias
                keyPassword = signing.keyPassword
            }
        }
    }

    buildTypes {
        release {
            isDebuggable = false
            // R8 full mode (AGP default): shrink, optimize and obfuscate code, then drop unused resources.
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
            signingConfig = signingConfigs.findByName("release")
        }
    }

    // Bytecode target. Built-in Kotlin follows this, so Java and Kotlin stay in sync.
    // Any JDK 17+ can run the build (CI uses 21, Android Studio bundles its own).
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    buildFeatures {
        compose = true
        buildConfig = true
    }

    lint {
        // Any lint warning fails the build. Exceptions must be justified in lint.xml.
        warningsAsErrors = true
        abortOnError = true
        checkDependencies = true
        lintConfig = file("lint.xml")
        sarifReport = true
    }

    testOptions {
        // Local unit tests run on the JVM with JUnit 5 (Jupiter); no Android plugin needed.
        unitTests.all { it.useJUnitPlatform() }
    }
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.lifecycle.runtime.compose)
    implementation(libs.androidx.lifecycle.viewmodel)
    implementation(libs.androidx.navigation.compose)
    implementation(libs.androidx.datastore.preferences)
    implementation(libs.androidx.biometric)
    implementation(libs.androidx.fragment)

    implementation(platform(libs.compose.bom))
    implementation(libs.compose.ui)
    implementation(libs.compose.material3)
    implementation(libs.compose.ui.tooling.preview)
    debugImplementation(libs.compose.ui.tooling)

    implementation(libs.kotlinx.coroutines.core)
    implementation(libs.kotlinx.collections.immutable)
    implementation(libs.kotlinx.serialization.json)

    implementation(libs.okhttp)
    implementation(libs.retrofit)
    implementation(libs.retrofit.kotlinx.serialization)
    implementation(libs.coil.compose)
    implementation(libs.coil.network.okhttp)
    implementation(libs.hilt.android)
    ksp(libs.hilt.compiler)
    implementation(libs.androidx.hilt.lifecycle.viewmodel.compose)

    testImplementation(platform(libs.junit5.bom))
    testImplementation(libs.junit5.jupiter)
    testImplementation(libs.kotlinx.coroutines.test)
    testImplementation(libs.turbine)
    testImplementation(libs.mockk)
    testRuntimeOnly(libs.junit5.platform.launcher)

    // Instrumented (on-device) Compose tests use JUnit 4 rules.
    androidTestImplementation(platform(libs.compose.bom))
    androidTestImplementation(libs.compose.ui.test.junit4)
    androidTestImplementation(libs.androidx.test.runner)
    androidTestImplementation(libs.androidx.test.ext.junit)
    debugImplementation(libs.compose.ui.test.manifest)
}
