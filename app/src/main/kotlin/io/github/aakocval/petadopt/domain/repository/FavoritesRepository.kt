package io.github.aakocval.petadopt.domain.repository

import kotlinx.coroutines.flow.Flow

/** The user's favorite pets, stored as ids so pet data and favorites can change independently. */
interface FavoritesRepository {
    fun observeFavoriteIds(): Flow<Set<String>>

    suspend fun setFavorite(
        petId: String,
        isFavorite: Boolean,
    )
}
