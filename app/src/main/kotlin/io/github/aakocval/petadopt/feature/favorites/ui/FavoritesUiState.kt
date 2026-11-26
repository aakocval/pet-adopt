package io.github.aakocval.petadopt.feature.favorites.ui

import io.github.aakocval.petadopt.core.ui.PetCardUi
import kotlinx.collections.immutable.ImmutableList

sealed interface FavoritesUiState {
    data object Loading : FavoritesUiState

    data object Empty : FavoritesUiState

    data class Pets(
        val pets: ImmutableList<PetCardUi>,
    ) : FavoritesUiState
}
