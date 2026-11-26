package io.github.aakocval.petadopt.domain.model

/** A pet together with whether the user has favorited it. */
data class FavoritablePet(
    val pet: Pet,
    val isFavorite: Boolean,
)
