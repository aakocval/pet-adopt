package io.github.aakocval.petadopt.feature.profile.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.aakocval.petadopt.core.common.AppInfo
import io.github.aakocval.petadopt.domain.repository.FavoritesRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel
    @Inject
    constructor(
        favoritesRepository: FavoritesRepository,
        appInfo: AppInfo,
    ) : ViewModel() {
        private val initial = ProfileUiState(appVersion = appInfo.versionName)

        val uiState: StateFlow<ProfileUiState> =
            favoritesRepository
                .observeFavoriteIds()
                .map { initial.copy(favoriteCount = it.size) }
                .stateIn(viewModelScope, SharingStarted.WhileSubscribed(STOP_TIMEOUT_MILLIS), initial)

        private companion object {
            const val STOP_TIMEOUT_MILLIS = 5_000L
        }
    }
