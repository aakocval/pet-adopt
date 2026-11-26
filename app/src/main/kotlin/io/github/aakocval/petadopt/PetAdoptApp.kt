package io.github.aakocval.petadopt

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

/**
 * Application entry point. Deliberately empty: nothing heavy runs at startup.
 * Debug-only tooling (StrictMode, LeakCanary) arrives in M8 via the debug source set.
 */
@HiltAndroidApp
class PetAdoptApp : Application()
