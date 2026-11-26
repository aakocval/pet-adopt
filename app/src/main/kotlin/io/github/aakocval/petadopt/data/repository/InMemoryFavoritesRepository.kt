package io.github.aakocval.petadopt.data.repository

import io.github.aakocval.petadopt.domain.repository.FavoritesRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject
import javax.inject.Singleton

/** Favorites kept for the app session. Persisted with DataStore in a later milestone. */
@Singleton
class InMemoryFavoritesRepository
    @Inject
    constructor() : FavoritesRepository {
        private val favoriteIds = MutableStateFlow(emptySet<String>())

        override fun observeFavoriteIds(): Flow<Set<String>> = favoriteIds.asStateFlow()

        override suspend fun setFavorite(
            petId: String,
            isFavorite: Boolean,
        ) {
            favoriteIds.update { if (isFavorite) it + petId else it - petId }
        }
    }
