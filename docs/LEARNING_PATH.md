# Learning Path

Each milestone is a tagged step in the history. Check out a tag to see the project at that
point (`git checkout v0.1.0`), or read the merge commit to see exactly what it added.

## M0 Foundations (v0.1.0)

**What this milestone teaches:** how to set up an Android project so quality is enforced
by tooling from the first commit, before there is any feature code to argue about.

- **One version catalog.** Every dependency and plugin version lives in
  `gradle/libs.versions.toml`. Modules reference `libs.*`, so an upgrade is a one-line change
  and Dependabot can open grouped PRs against a single file.
- **AGP 9 built-in Kotlin.** There is no `kotlin-android` plugin anymore; AGP compiles Kotlin
  itself and follows `compileOptions` for the JVM target.
- **Dispatchers are injected from day one.** `DispatchersModule` is the only file that touches
  `Dispatchers.IO`. Everything else asks for `@Dispatcher(AppDispatcher.IO)`, which is what
  makes ViewModels and repositories testable with a `TestDispatcher` later.
- **Three linters, three jobs.** ktlint owns formatting (auto-fixable), detekt owns code smells
  and complexity, Android Lint owns platform correctness. Lint runs with warnings as errors, so
  suppressions have to be written down with a reason in `app/lint.xml`.
- **CI is the contract.** The workflow runs the same Gradle tasks you run locally. Actions are
  pinned to commit SHAs and the Gradle wrapper is checksum-validated, so the build can't be
  changed underneath you.
- **Secure defaults before features.** Backups off, only the launcher exported, secrets
  git-ignored. Cheap to do now, painful to retrofit.

## M1 Design system (v0.2.0)

**What this milestone teaches:** how to build a small design system so screens describe
*what* they show, never *how* it looks.

- **Tokens, then theme, then components.** Raw colors in `Color.kt` are `internal`; screens
  read `MaterialTheme.colorScheme` or `PetAdoptTheme.colors`. Dark mode is one switch in
  `PetAdoptTheme`, not an `if` in every screen.
- **Extending Material 3 without fighting it.** Pastel tiles and the favorite color have no M3
  slot, so they live in an `@Immutable` `PetAdoptColors` provided through a
  `staticCompositionLocalOf`, exposed as `PetAdoptTheme.colors`, the same pattern
  `MaterialTheme` uses internally.
- **Deterministic visuals.** `tileIndexFor(key)` uses `String.hashCode` (specified by the JVM)
  masked to non-negative, so a pet keeps its color everywhere. The test covers the classic
  `abs(Int.MIN_VALUE)` trap.
- **Components take values and lambdas, not domain models.** `PetTile(name, subtitle, ...)`
  doesn't know what a `Pet` is. That keeps the design system reusable, previewable with
  literals, and made of stable parameters, so Compose can skip recomposition.
- **Slots for what changes later.** `PetTile`'s `image` slot defaults to a placeholder; Coil
  plugs in during M6 without touching the component.
- **Accessibility is part of the component.** `FavoriteButton` is a real toggle (announced
  as on/off, 48dp target), `LargeTitle` is a semantic heading, decorative icons have no
  content description.
- **Vector drawables over icon libraries.** Shipping only the six icons used keeps the APK
  small, and every icon's license is recorded in its file.
