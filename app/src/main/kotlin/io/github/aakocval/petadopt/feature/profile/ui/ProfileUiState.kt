package io.github.aakocval.petadopt.feature.profile.ui

/**
 * Profile content. The user is always a guest until sign-in arrives with the auth gate (M4),
 * so there's no loading state: everything here is local and immediate.
 */
data class ProfileUiState(
    val favoriteCount: Int = 0,
    val appVersion: String = "",
)
