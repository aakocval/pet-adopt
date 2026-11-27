# Pet Adopt

A native Android showcase app in Kotlin: **MVVM with unidirectional data flow**, type-safe
**Navigation Compose**, and a clean data layer, with security and performance treated as
first-class concerns.

> Status: early. Built milestone by milestone; see [CHANGELOG.md](CHANGELOG.md) for what each
> step adds.

## Stack

Kotlin · Jetpack Compose · Material 3 · Navigation Compose · Hilt · Coroutines/Flow ·
JUnit 5 · ktlint · detekt · Android Lint · GitHub Actions

## Getting started

Requirements: Android Studio (recent stable) or JDK 17+ with the Android SDK.

```bash
git clone <this repo>
cd pet-adopt
./gradlew assembleDebug
```

No API keys are needed. The app runs fully offline with bundled mock data.

## Quality checks

The same commands CI runs:

```bash
./gradlew ktlintCheck detekt lintDebug testDebugUnitTest
```

`./gradlew ktlintFormat` fixes formatting.

## Project layout

```
app/src/main/kotlin/io/github/aakocval/petadopt/
├── core/      # design system, shared helpers
├── data/      # repositories, local + remote sources
├── domain/    # pure Kotlin models and repository interfaces
├── di/        # Hilt modules
└── feature/   # one folder per feature: ui/ + navigation/
```

Architecture docs, diagrams and screenshots land in the final milestone.

## License

[MIT](LICENSE)
