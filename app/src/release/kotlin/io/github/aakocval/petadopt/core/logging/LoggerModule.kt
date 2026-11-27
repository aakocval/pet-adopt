package io.github.aakocval.petadopt.core.logging

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

/** Release builds: logging is compiled down to nothing. */
@Module
@InstallIn(SingletonComponent::class)
object LoggerModule {
    @Provides
    fun providesLogger(): AppLogger = NoOpLogger
}
