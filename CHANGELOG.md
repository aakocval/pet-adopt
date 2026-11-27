# Changelog

All notable changes to this project are documented here.
The format follows [Keep a Changelog](https://keepachangelog.com/en/1.1.0/), and the project
uses [Semantic Versioning](https://semver.org/).

## [Unreleased]

## [0.7.0] - 2026-11-27

M6 Explore and data: a real data layer (bundled catalog, remote photos, offline cache) and the map.

### Added
- Pet catalog (names, ages, bios, shelters, coordinates) loaded from `assets/pets.json`
- Remote photos: Dog CEO (by breed) and Cataas through Retrofit + kotlinx.serialization, with
  allow-listed request paths and HTTPS host checks on every returned URL
- `OfflineFirstPetRepository`: emits the local catalog immediately, fetches missing photos in an
  app-scoped coroutine (dogs in parallel), caches photo URLs in a file, and retries on the next
  subscription after an offline start
- Coil 3 image loader sharing the app's OkHttp client, with memory and disk caches and crossfade,
  created lazily on first use
- Illustrated dog and cat placeholder faces (original vector drawables) under every photo
- Explore: `MapProvider` interface with a Compose Canvas mock map (roads, parks, water, pins), a
  pet card for the selected pin, and Pet Detail inside the Explore tab; see `docs/MAP.md`
- `INTERNET` permission (the only permission the app requests)

### Removed
- In-memory pet repository and hard-coded seed data

### Fixed
- Tiles and Pet Detail dropped the species and photo URL (cats showed dog faces, no photos)
- Map pins for pets at the same shelter overlapped

## [0.6.0] - 2026-11-27

M5 Flow state: a three-step adoption application with one shared, graph-scoped ViewModel.

### Added
- Adoption flow (Form -> Review -> Success) as a nested graph inside each tab, started from
  "Apply to adopt" on Pet Detail; the bottom bar hides while it's open
- `AdoptionViewModel` scoped to the flow's nav graph entry, with the pet id passed by assisted
  injection; the draft is written through to `SavedStateHandle` and survives process death
- Domain rules: `ApplicationInputSanitizer` (per keystroke) and `AdoptionApplicationValidator`
  (on continue and again before submit), plus a normalized `AdoptionApplication` model
- Form with autofill hints, live errors after the first attempt, character counter, home type
  chips, other-pets switch and home-visit consent; Review summary; Success with a reference
- Fake adoption repository returning readable references (no 0/O or 1/I)
- Tests for the sanitizer, validator, repository and ViewModel (including a process-death restore)

### Changed
- Commit messages now use `[Type] Description` (e.g. `[Feat] Add ...`)

### Fixed
- The adoption form ignored input until the pet had loaded
- Fast typing in the adoption form could scramble text (state is now updated synchronously)
- The review message card now spans the full width

## [0.5.0] - 2026-11-26

M4 Nested graphs: onboarding, an auth gate, and a root that switches graphs.

### Added
- Root navigation: `RootViewModel` maps onboarding + session state to Onboarding, Auth or Main;
  `RootNavHost` swaps graphs and clears the whole back stack on every switch
- Onboarding graph: three swipeable pages with Skip / Next / Get started, shown once
- Auth graph: login screen with validation, input sanitizing, double-submit guard, autofill
  hints, show/hide password and an accessible error message
- Fake auth repository: any valid email + 8-character password; session persisted in DataStore
  (display name and email only, never the password)
- DataStore-backed user preferences on the injected IO dispatcher
- Pure-Kotlin `EmailValidator` and `PasswordPolicy` in the domain layer
- Profile shows the signed-in user and has Sign out
- Tests: preferences and auth against a real temp-file DataStore, login and root ViewModels,
  and instrumented Compose tests for root back-stack clearing
- CI compiles instrumented tests

### Fixed
- Profile says "None yet" instead of "0 pets"

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

[Unreleased]: ../../compare/v0.7.0...HEAD
[0.7.0]: ../../compare/v0.6.0...v0.7.0
[0.6.0]: ../../compare/v0.5.0...v0.6.0
[0.5.0]: ../../compare/v0.4.0...v0.5.0
[0.4.0]: ../../compare/v0.3.0...v0.4.0
[0.3.0]: ../../compare/v0.2.0...v0.3.0
[0.2.0]: ../../compare/v0.1.0...v0.2.0
[0.1.0]: ../../releases/tag/v0.1.0
