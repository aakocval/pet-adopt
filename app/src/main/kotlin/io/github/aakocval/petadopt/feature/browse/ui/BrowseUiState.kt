package io.github.aakocval.petadopt.feature.browse.ui

import io.github.aakocval.petadopt.core.ui.PetCardUi
import io.github.aakocval.petadopt.domain.model.Pet
import io.github.aakocval.petadopt.domain.model.Species
import kotlinx.collections.immutable.ImmutableList

/**
 * Everything the Browse screen renders, as one immutable value.
 *
 * Only immutable types (data classes, enums, [ImmutableList]), so the Compose compiler
 * infers it as stable and skips recomposition when nothing changed. No Compose import needed.
 */
data class BrowseUiState(
    val filter: SpeciesFilter = SpeciesFilter.All,
    val content: BrowseContent = BrowseContent.Loading,
)

/** Mutually exclusive screen states: "loading with an error and data" can't be represented. */
sealed interface BrowseContent {
    data object Loading : BrowseContent

    data object Error : BrowseContent

    data class Pets(
        val pets: ImmutableList<PetCardUi>,
    ) : BrowseContent
}

enum class SpeciesFilter {
    All,
    Dogs,
    Cats,
    ;

    fun matches(pet: Pet): Boolean =
        when (this) {
            All -> true
            Dogs -> pet.species == Species.Dog
            Cats -> pet.species == Species.Cat
        }
}

/** One-off effects the UI performs once (e.g. a snackbar), never replayed on rotation. */
sealed interface BrowseEvent {
    data class FavoriteChanged(
        val petName: String,
        val isFavorite: Boolean,
    ) : BrowseEvent
}
