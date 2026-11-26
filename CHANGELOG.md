# Changelog

All notable changes to this project are documented here.
The format follows [Keep a Changelog](https://keepachangelog.com/en/1.1.0/), and the project
uses [Semantic Versioning](https://semver.org/).

## [Unreleased]

## [0.2.0] - 2026-11-26

M1 Design system: the visual language every screen builds on.

### Added
- `PetAdoptTheme` with light and dark color schemes, one tangerine accent and six pastel tiles
  (muted counterparts in dark mode); dynamic color intentionally off
- iOS-like type scale mapped onto Material 3 slots, using the system font
- Shape and spacing tokens on a 4dp grid
- Stable per-pet tile colors (`PetAdoptTheme.colors.tileFor(id)`)
- Components with light/dark previews: `LargeTitle`, `PetTile` (+ `FavoriteButton`,
  `PetPlaceholder`), `ListRow`, `PetAdoptBottomBar`
- Vector icon set (`PetAdoptIcons`): original paw, plus Material Icons (Apache 2.0)
- Temporary design system gallery as the launch screen (replaced in M3)
- Window background matches the Compose theme in light and dark (no launch flash)

## [0.1.0] - 2026-11-26

M0 Foundations: a buildable, checked, empty app.

### Added
- Gradle 9.9 Kotlin DSL project with a version catalog (`gradle/libs.versions.toml`) and a
  checksum-pinned wrapper
- Single-activity Compose app (AGP 9 built-in Kotlin, compileSdk 37.2, minSdk 26)
- Hilt with an empty `Application` and injectable coroutine dispatchers
- JUnit 5 unit tests
- ktlint (`ktlint_official`), detekt with Compose-aware rules, Android Lint with warnings as errors
- GitHub Actions CI: ktlint, detekt, lint, unit tests, debug + R8 release build, SARIF upload
- Dependabot for Gradle and GitHub Actions
- MIT license, README, SECURITY.md, PR and issue templates

### Security
- Backups disabled with explicit data extraction rules; only the launcher activity is exported
- Secrets, keystores and `local.properties` git-ignored

[Unreleased]: ../../compare/v0.2.0...HEAD
[0.2.0]: ../../compare/v0.1.0...v0.2.0
[0.1.0]: ../../releases/tag/v0.1.0
