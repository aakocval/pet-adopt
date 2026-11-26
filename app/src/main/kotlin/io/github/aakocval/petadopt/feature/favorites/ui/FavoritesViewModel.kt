package io.github.aakocval.petadopt.feature.favorites.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.aakocval.petadopt.core.ui.toCardUi
import io.github.aakocval.petadopt.domain.repository.FavoritesRepository
import io.github.aakocval.petadopt.domain.usecase.ObservePetsWithFavoritesUseCase
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

/** Favorites: the same pets-with-favorites stream as Browse, narrowed to favorited pets. */
@HiltViewModel
class FavoritesViewModel
    @Inject
    constructor(
        observePetsWithFavorites: ObservePetsWithFavoritesUseCase,
        private val favoritesRepository: FavoritesRepository,
    ) : ViewModel() {
        val uiState: StateFlow<FavoritesUiState> =
            observePetsWithFavorites()
                .map { pets ->
                    val favorites = pets.filter { it.isFavorite }
                    if (favorites.isEmpty()) {
                        FavoritesUiState.Empty
                    } else {
                        FavoritesUiState.Pets(favorites.map { it.toCardUi() }.toImmutableList())
                    }
                }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(STOP_TIMEOUT_MILLIS), FavoritesUiState.Loading)

        fun onRemoveFavorite(petId: String) {
            viewModelScope.launch { favoritesRepository.setFavorite(petId, isFavorite = false) }
        }

        private companion object {
            const val STOP_TIMEOUT_MILLIS = 5_000L
        }
    }
