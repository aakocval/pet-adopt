package io.github.aakocval.petadopt.core.common

import javax.inject.Qualifier

/**
 * Qualifier for injected coroutine dispatchers.
 *
 * Classes ask for `@Dispatcher(AppDispatcher.IO) CoroutineDispatcher` instead of calling
 * `Dispatchers.IO` directly, so tests can swap in a `TestDispatcher`.
 */
@Qualifier
@Retention(AnnotationRetention.RUNTIME)
annotation class Dispatcher(
    val dispatcher: AppDispatcher,
)

enum class AppDispatcher { Default, IO }
