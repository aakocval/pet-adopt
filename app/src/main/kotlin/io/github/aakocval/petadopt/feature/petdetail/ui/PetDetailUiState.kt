package io.github.aakocval.petadopt.feature.petdetail.ui

import io.github.aakocval.petadopt.domain.model.Pet
import io.github.aakocval.petadopt.domain.model.Sex
import io.github.aakocval.petadopt.domain.model.Species

sealed interface PetDetailUiState {
    data object Loading : PetDetailUiState

    /** The id doesn't match any pet: a stale link or an adopted pet. */
    data object NotFound : PetDetailUiState

    data class Success(
        val pet: PetDetailUi,
    ) : PetDetailUiState
}

data class PetDetailUi(
    val id: String,
    val name: String,
    val species: Species,
    val breed: String,
    val ageInMonths: Int,
    val sex: Sex,
    val bio: String,
    val shelterName: String,
    val isFavorite: Boolean,
    val photoUrl: String? = null,
)

internal fun Pet.toDetailUi(isFavorite: Boolean) =
    PetDetailUi(
        id = id,
        name = name,
        species = species,
        breed = breed,
        ageInMonths = ageInMonths,
        sex = sex,
        bio = bio,
        shelterName = shelterName,
        isFavorite = isFavorite,
    )
