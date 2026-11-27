package io.github.aakocval.petadopt.feature.explore.ui

import io.github.aakocval.petadopt.domain.model.Species
import io.github.aakocval.petadopt.feature.explore.map.MapPin
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf

data class ExploreUiState(
    val isLoading: Boolean = true,
    val pins: ImmutableList<MapPin> = persistentListOf(),
    /** The pet whose pin is selected, shown in the bottom card; `null` hides the card. */
    val selected: ExplorePetCard? = null,
)

data class ExplorePetCard(
    val id: String,
    val name: String,
    val breed: String,
    val ageInMonths: Int,
    val species: Species,
    val shelterName: String,
    val photoUrl: String?,
)
