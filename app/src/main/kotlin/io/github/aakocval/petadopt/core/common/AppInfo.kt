package io.github.aakocval.petadopt.core.common

/** Build facts the UI shows, injected so ViewModels never read BuildConfig directly. */
data class AppInfo(
    val versionName: String,
)
