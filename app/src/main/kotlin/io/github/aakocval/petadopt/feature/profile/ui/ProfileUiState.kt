package io.github.aakocval.petadopt.feature.profile.ui

/** Profile content. Everything is local and immediate, so there's no loading state. */
data class ProfileUiState(
    val userName: String? = null,
    val userEmail: String? = null,
    val favoriteCount: Int = 0,
    val appVersion: String = "",
)
