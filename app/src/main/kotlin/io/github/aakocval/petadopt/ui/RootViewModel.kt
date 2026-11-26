package io.github.aakocval.petadopt.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.aakocval.petadopt.domain.repository.AuthRepository
import io.github.aakocval.petadopt.domain.repository.UserPreferencesRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

/** Which top-level graph the app should show. */
enum class RootDestination { Onboarding, Auth, Main }

sealed interface RootUiState {
    /** Preferences and session are still being read; the window background shows meanwhile. */
    data object Loading : RootUiState

    data class Ready(
        val destination: RootDestination,
    ) : RootUiState
}

/**
 * The app's gatekeeper. Onboarding and auth state are the single source of truth for which
 * graph is visible: screens change state (finish onboarding, sign in, sign out) and the root
 * reacts, so no screen ever navigates across graphs itself.
 */
@HiltViewModel
class RootViewModel
    @Inject
    constructor(
        userPreferences: UserPreferencesRepository,
        authRepository: AuthRepository,
    ) : ViewModel() {
        val uiState: StateFlow<RootUiState> =
            combine(
                userPreferences.hasCompletedOnboarding,
                authRepository.currentUser.map { it != null },
                ::rootDestinationFor,
            ).distinctUntilChanged()
                .map<RootDestination, RootUiState> { RootUiState.Ready(it) }
                // Eagerly: the activity needs the answer before the first frame, every time.
                .stateIn(viewModelScope, SharingStarted.Eagerly, RootUiState.Loading)
    }

/** Onboarding always comes first; after that, the session decides. */
fun rootDestinationFor(
    hasCompletedOnboarding: Boolean,
    isSignedIn: Boolean,
): RootDestination =
    when {
        !hasCompletedOnboarding -> RootDestination.Onboarding
        !isSignedIn -> RootDestination.Auth
        else -> RootDestination.Main
    }
