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
