package io.github.aakocval.petadopt.feature.petdetail.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.aakocval.petadopt.domain.repository.FavoritesRepository
import io.github.aakocval.petadopt.domain.repository.PetRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

/**
 * Pet Detail logic. The pet id arrives through Hilt assisted injection: the route reads it from
 * the type-safe nav route and passes it in, so this class never touches SavedStateHandle or
 * navigation APIs, and tests just call the constructor.
 */
@HiltViewModel(assistedFactory = PetDetailViewModel.Factory::class)
class PetDetailViewModel
    @AssistedInject
    constructor(
        @Assisted private val petId: String,
        petRepository: PetRepository,
        private val favoritesRepository: FavoritesRepository,
    ) : ViewModel() {
        val uiState: StateFlow<PetDetailUiState> =
            combine(petRepository.observePet(petId), favoritesRepository.observeFavoriteIds()) { pet, favoriteIds ->
                if (pet == null) {
                    PetDetailUiState.NotFound
                } else {
                    PetDetailUiState.Success(pet.toDetailUi(isFavorite = pet.id in favoriteIds))
                }
            }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(STOP_TIMEOUT_MILLIS), PetDetailUiState.Loading)

        fun onToggleFavorite(isFavorite: Boolean) {
            viewModelScope.launch { favoritesRepository.setFavorite(petId, isFavorite) }
        }

        @AssistedFactory
        interface Factory {
            fun create(petId: String): PetDetailViewModel
        }

        private companion object {
            const val STOP_TIMEOUT_MILLIS = 5_000L
        }
    }
