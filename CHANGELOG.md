# Changelog

All notable changes to this project are documented here.
The format follows [Keep a Changelog](https://keepachangelog.com/en/1.1.0/), and the project
uses [Semantic Versioning](https://semver.org/).

## [Unreleased]

## [0.4.0] - 2026-11-26

M3 Navigation depth: four tabs, each with its own back stack, and Pet Detail.

### Added
- `AppNavHost`, `PetAppState` and `PetApp` shell: bottom tabs as nested graphs, tab switches
  that save and restore each tab's back stack, re-tap a tab to pop to its root
- Pet Detail: assisted-injected `PetDetailViewModel` (pet id passed in, no `SavedStateHandle`),
  photo header, quick facts, bio, shelter, favorite toggle, not-found state
- Favorites tab with an animated grid and empty state
- Profile tab (guest header, live saved-pets count that jumps to Favorites, app version)
- Placeholder Explore tab (map arrives in M6)
- `ObservePetsWithFavoritesUseCase`, shared by Browse and Favorites
- `BackButton` design system component; chevrons mirror in right-to-left layouts
- Injectable `AppInfo` for build facts

### Changed
- Pet tile UI model and age formatting moved to `core/ui` so features don't import each other

### Fixed
- Pet photo header no longer fills the screen in landscape
- A fast double tap on Back no longer pops the tab's root screen

## [0.3.0] - 2026-11-26

M2 First vertical slice: Browse, end to end. The reference every other feature copies.

### Added
- Domain: `Pet` model and `PetRepository` / `FavoritesRepository` interfaces (pure Kotlin)
- Data: in-memory repositories with 12 fictional pets, bound with Hilt
- `BrowseViewModel`: one immutable `StateFlow<BrowseUiState>` (`WhileSubscribed(5s)`),
  species filter, favorites, error + retry, and a buffered channel for one-off events
- `BrowseRoute` (stateful) + `BrowseScreen` (stateless, previewable) with loading, empty and
  error states, keyed lazy grid, pluralized ages and a favorite snackbar
- Type-safe navigation: `@Serializable` routes and a `browseGraph(...)` extension with a
  per-tab nested graph and a `nestedGraphs` slot
- Lifecycle-aware `ObserveAsEvents` helper
- Tests: repositories (Turbine), ViewModel (fakes + MockK, JUnit 5 Main dispatcher extension)

### Removed
- Temporary design system gallery launch screen

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

[Unreleased]: ../../compare/v0.4.0...HEAD
[0.4.0]: ../../compare/v0.3.0...v0.4.0
[0.3.0]: ../../compare/v0.2.0...v0.3.0
[0.2.0]: ../../compare/v0.1.0...v0.2.0
[0.1.0]: ../../releases/tag/v0.1.0
