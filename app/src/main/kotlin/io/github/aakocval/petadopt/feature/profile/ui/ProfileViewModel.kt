package io.github.aakocval.petadopt.feature.profile.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.aakocval.petadopt.core.common.AppInfo
import io.github.aakocval.petadopt.domain.repository.AuthRepository
import io.github.aakocval.petadopt.domain.repository.FavoritesRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel
    @Inject
    constructor(
        private val authRepository: AuthRepository,
        favoritesRepository: FavoritesRepository,
        appInfo: AppInfo,
    ) : ViewModel() {
        private val initial = ProfileUiState(appVersion = appInfo.versionName)

        val uiState: StateFlow<ProfileUiState> =
            combine(authRepository.currentUser, favoritesRepository.observeFavoriteIds()) { user, favoriteIds ->
                initial.copy(userName = user?.displayName, userEmail = user?.email, favoriteCount = favoriteIds.size)
            }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(STOP_TIMEOUT_MILLIS), initial)

        /** Only clears the session. The app root sees it and swaps to the auth graph, clearing the back stack. */
        fun onSignOut() {
            viewModelScope.launch { authRepository.signOut() }
        }

        private companion object {
            const val STOP_TIMEOUT_MILLIS = 5_000L
        }
    }
