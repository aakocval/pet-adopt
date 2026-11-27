package io.github.aakocval.petadopt.core.common

import javax.inject.Qualifier

/** A CoroutineScope that lives as long as the app, for work that must outlive a screen (e.g. a cache refresh). */
@Qualifier
@Retention(AnnotationRetention.RUNTIME)
annotation class ApplicationScope
