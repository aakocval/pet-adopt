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

## M2 First vertical slice (v0.3.0)

**What this milestone teaches:** the shape of one complete feature, from domain model to
pixels, so every later feature is "copy Browse, change the nouns".

```
feature/browse/
├── navigation/BrowseNavigation.kt   @Serializable routes + NavGraphBuilder.browseGraph(...)
└── ui/
    ├── BrowseUiState.kt             immutable state, sealed content, one-off events
    ├── BrowseViewModel.kt           state holder: actions in, StateFlow out
    ├── BrowseRoute.kt               stateful: ViewModel, lifecycle collection, events
    └── BrowseScreen.kt              stateless: UiState + lambdas, previews
```

- **Dependencies point inward.** `domain/` has the model and repository *interfaces*;
  `data/` implements them; `di/DataModule` binds one to the other. The ViewModel only sees
  interfaces, so M6 can swap in an offline-first repository without touching Browse.
- **No use case yet, on purpose.** Combining pets with favorites is a single `combine`; a
  use-case class would only forward calls. Use cases arrive when logic is shared or non-trivial.
- **One UiState, impossible states unrepresentable.** `BrowseContent` is `Loading | Error |
  Pets`, so "loading with an error and stale data" can't happen by accident. Only immutable
  types (`ImmutableList`, data classes) are used, so Compose infers stability without the
  ViewModel importing Compose.
- **State vs events.** State is replayed (a `StateFlow`); one-off effects like snackbars go
  through a buffered `Channel`, delivered exactly once and only while the screen is STARTED.
  The ViewModel sends *data* (`FavoriteChanged(name)`); the route turns it into a localized string.
- **`stateIn(WhileSubscribed(5_000))`** keeps work alive through a rotation but stops it
  5 seconds after the app is backgrounded. `collectAsStateWithLifecycle` is the other half.
- **Route/screen split.** `BrowseRoute` is the only part that knows about Hilt and lifecycle;
  `BrowseScreen` takes values and lambdas, which is what makes it previewable and UI-testable.
- **Features don't know each other.** `browseGraph(onPetClick = ...)` takes navigation as
  lambdas, and each tab gets its own nested graph (`BrowseGraph`), which is how per-tab back
  stacks work in M3.
- **Testing flows deterministically.** Collect `uiState` in `backgroundScope` with an
  `UnconfinedTestDispatcher` and assert on `uiState.value`; a fake repository that emits
  only when told makes "loading" observable. Turbine is for streams of events, MockK only for
  pure interaction checks, hand-written fakes for anything stateful.

## M3 Navigation depth (v0.4.0)

**What this milestone teaches:** how to scale navigation to several features without them
knowing about each other, and how per-tab back stacks actually work in Navigation Compose.

```
root (AppNavHost)
├── BrowseGraph ──── Browse ─> PetDetail(petId)
├── ExploreGraph ─── Explore
├── FavoritesGraph ─ Favorites ─> PetDetail(petId)
└── ProfileGraph ─── Profile ─(Saved pets)─> Favorites tab
```

- **One nested graph per tab.** A tab switch is `popUpTo(start) { saveState = true }` +
  `restoreState = true` + `launchSingleTop`. The tab you leave is saved, not destroyed, and comes
  back exactly as you left it. All of it is in `PetAppState`, not scattered across screens.
- **The same screen in two tabs.** `petDetailScreen()` is registered inside both Browse's and
  Favorites' graphs (via each feature's `nestedGraphs` slot). Navigating to `PetDetailDestination`
  resolves inside the current tab's graph, so the detail lands on that tab's back stack and the
  right tab stays highlighted.
- **Features stay independent.** Features expose `NavGraphBuilder` extensions and take
  navigation as lambdas. `AppNavHost` is the only file that knows "pet clicked" means "push
  Pet Detail", and "Saved pets" means "switch to the Favorites tab".
- **Arguments without SavedStateHandle.** The route reads `petId` from the type-safe route and
  passes it to `PetDetailViewModel` through Hilt assisted injection
  (`hiltViewModel<VM, Factory> { it.create(petId) }`). The ViewModel has no navigation import,
  and tests just call its constructor.
- **When a use case earns its place.** Browse and Favorites both join pets with favorites, so
  that join became `ObservePetsWithFavoritesUseCase`: one tested implementation, two callers.
  Shared UI pieces (`PetCardUi`, age formatting) moved to `core/ui` for the same reason.
- **Two real bugs, found on a device.** In landscape, a full-width photo filled the screen
  (fixed with a height cap and height-first `aspectRatio`). A fast double tap on Back popped
  twice and left a blank tab (fixed with `dropUnlessResumed`, which ignores clicks while a
  destination animates out). Unit tests wouldn't have caught either.

## M4 Nested graphs (v0.5.0)

**What this milestone teaches:** how to gate an app behind onboarding and sign-in without
any screen navigating across graphs, and how to make sure Back never leaks between them.

```
RootNavHost (driven by RootViewModel)
├── OnboardingGraph ── Onboarding (3 pages)
├── AuthGraph ──────── Login
└── MainDestination ── PetApp (bottom tabs, own nested NavHost)
```

- **State drives navigation, not screens.** Finishing onboarding writes a preference; signing
  in or out changes the session. `RootViewModel` combines both into one `RootDestination`
  (a pure function, `rootDestinationFor`), and the root swaps graphs when it changes. Login
  and Profile never call `navigate`.
- **Clearing the back stack on every switch.** `switchRootTo` navigates with
  `popUpTo(graph.id) { inclusive = true }`, so the new graph is the only thing left. After
  signing in, Back leaves the app instead of returning to login; after signing out, Back can't
  reach the app. Switching to the graph already showing is a no-op, so recompositions and
  rotations don't reset anything.
- **A nested NavHost for the main app.** The tabs keep their own `NavHostController` inside the
  `MainDestination`. Signing out destroys it, so the next user starts with fresh tabs.
- **Validation where it belongs.** Format rules (`EmailValidator`, `PasswordPolicy`) are pure
  Kotlin in the domain, shared by the form and the backend. The ViewModel sanitizes as you type
  (no whitespace, length caps), validates on submit, reports errors as enums (the screen picks
  the words), and ignores a second tap while a request is in flight.
- **Handle credentials carefully.** The password lives only in in-memory UI state, is cleared
  after a successful sign-in, and is never logged or persisted. Autofill hints let password
  managers fill the form. (Keystore-backed session storage comes in M7.)
- **Testing navigation at two levels.** JVM tests cover the decision (`rootDestinationFor`, the
  full onboarding -> sign in -> sign out lifecycle). An instrumented Compose test drives the
  real `switchRootTo` against the real routes and asserts nothing is left to go back to. It
  replaces screens with labels, so it needs no Hilt test setup.
- **Testing DataStore for real.** Repository tests use an actual Preferences DataStore in a
  JUnit 5 `@TempDir`, so serialization and keys are exercised, not mocked.

## M5 Flow state (v0.6.0)

**What this milestone teaches:** how to build a multi-step flow whose screens share one piece
of state, and how to validate user input properly without fighting the person typing.

```
PetDetail ─(Apply)─> AdoptionGraph(petId)          ← owns AdoptionViewModel
                     ├── Form ─(valid)─> Review ─(sent)─> Success
                     └── Success pops Form + Review; Done/Back leaves the whole graph
```

- **A ViewModel scoped to a nav graph.** Each step asks for the ViewModel from the
  `AdoptionGraph` back stack entry (`hiltViewModel(viewModelStoreOwner = graphEntry)`), not from
  its own entry. All three screens share one instance; popping the graph clears it.
- **Surviving process death.** The draft is written through to `SavedStateHandle` as plain
  primitives (the domain draft stays Parcelable-free). A test rebuilds the ViewModel from the
  same handle; on the emulator, killing the backgrounded process and relaunching restored both
  the back stack and every field.
- **Sanitize vs validate.** `ApplicationInputSanitizer` runs on every keystroke and only removes
  what can never be valid (control characters, letters in a phone number, input past the cap). It
  never trims, so you can still type a space between names. `AdoptionApplicationValidator` runs
  on the trimmed draft when you continue, and *again* before submitting: never trust that the
  previous screen validated. The only way to build an `AdoptionApplication` is through it.
- **Errors that don't nag.** Nothing is red until the first "Review application"; after that,
  errors update live as you fix them. Errors are enums; the screen picks localized words.
- **Text fields need synchronous state.** The first version derived UI state with `combine()`.
  It passed every unit test, but on a device fast typing came out scrambled ("hoall day.m"),
  because each keystroke took a few async hops before the field saw its own value. The fix keeps
  one `MutableStateFlow` updated synchronously in each action (as the login form already did).
- **Steps as events, exits as navigation rules.** The ViewModel emits `ShowReview` and
  `Submitted`; the graph decides where to go. Reaching Success pops Form and Review, so Back can't
  reopen a sent application.

## M6 Explore and data (v0.7.0)

**What this milestone teaches:** how to build an offline-first data layer that's honest about
the network, and how to hide a third-party UI dependency (a map) behind an interface.

```
OfflineFirstPetRepository
├── PetCatalogSource ── assets/pets.json          (always local, instant)
├── PhotoUrlCache ───── files/pet_photo_urls.json (id -> URL, survives restarts)
└── RemotePhotoSource ─ Dog CEO + Cataas (Retrofit) ── Coil disk cache holds the bytes
```

- **Local first, network second.** The first emission is the bundled catalog plus cached photo
  URLs, so the list appears instantly with or without a connection. Photos are fetched afterwards
  in an `@ApplicationScope` coroutine (so leaving a screen doesn't cancel them), dogs in
  parallel with `async`/`awaitAll`.
- **Fail soft, and narrowly.** Only network, HTTP and malformed-response errors become "no photo";
  anything else is a bug and propagates. A pet without a photo shows an illustrated face, and the
  next subscriber retries, so an offline first launch heals itself.
- **Cache what's expensive, not what's authoritative.** Only photo URLs are cached. The catalog
  always comes from the app, so an update to `pets.json` in a new release is never shadowed by a
  stale cache. Writes are atomic (temp file + rename).
- **Treat API responses as untrusted input.** The breed path is allow-listed before it's put in a
  URL (it's sent `encoded = true`), and returned image URLs must be HTTPS on the expected host.
- **One OkHttp client.** Retrofit and Coil share it: one connection pool, one place to add
  certificate pinning in M7. Coil's loader is built lazily (`dagger.Lazy`), keeping app startup empty.
- **A map behind an interface.** `MapProvider` has one `@Composable` function. The Canvas mock
  needs no API key; Google Maps or MapLibre is one new class and one changed Hilt binding
  (`docs/MAP.md`). The Canvas exposes each pin to TalkBack as a custom action.
- **Defaults can hide bugs.** `PetCardUi` briefly had default values for `species` and
  `photoUrl`. A mapper silently skipped them, every test still passed, and on a device every cat
  had a dog face and no tile had a photo. Removing the defaults made the compiler point at every
  call site; a regression test now pins the mapping.
