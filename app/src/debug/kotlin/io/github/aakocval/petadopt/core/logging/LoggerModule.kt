package io.github.aakocval.petadopt.core.logging

import android.util.Log
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

/** Debug builds only: this file doesn't exist in release, so release code can't log to logcat. */
@Module
@InstallIn(SingletonComponent::class)
object LoggerModule {
    @Provides
    fun providesLogger(): AppLogger = LogcatLogger
}

private object LogcatLogger : AppLogger {
    override fun debug(
        tag: String,
        message: String,
    ) {
        Log.d("PetAdopt/$tag", message)
    }

    override fun warn(
        tag: String,
        message: String,
    ) {
        Log.w("PetAdopt/$tag", message)
    }
}
