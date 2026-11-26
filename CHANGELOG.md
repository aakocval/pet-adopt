# Changelog

All notable changes to this project are documented here.
The format follows [Keep a Changelog](https://keepachangelog.com/en/1.1.0/), and the project
uses [Semantic Versioning](https://semver.org/).

## [Unreleased]

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

[Unreleased]: ../../compare/v0.1.0...HEAD
[0.1.0]: ../../releases/tag/v0.1.0
