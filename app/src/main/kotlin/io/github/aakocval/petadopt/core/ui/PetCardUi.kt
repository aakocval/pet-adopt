package io.github.aakocval.petadopt.core.ui

import io.github.aakocval.petadopt.domain.model.FavoritablePet

/**
 * What one pet tile needs, shared by every screen that shows pet tiles (Browse, Favorites).
 * Formatting (age, subtitle) stays in Compose, where resources and locale are available.
 */
data class PetCardUi(
    val id: String,
    val name: String,
    val breed: String,
    val ageInMonths: Int,
    val isFavorite: Boolean,
)

fun FavoritablePet.toCardUi() =
    PetCardUi(
        id = pet.id,
        name = pet.name,
        breed = pet.breed,
        ageInMonths = pet.ageInMonths,
        isFavorite = isFavorite,
    )
