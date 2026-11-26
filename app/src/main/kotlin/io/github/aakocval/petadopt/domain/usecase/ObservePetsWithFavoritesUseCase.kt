package io.github.aakocval.petadopt.domain.usecase

import io.github.aakocval.petadopt.domain.model.FavoritablePet
import io.github.aakocval.petadopt.domain.repository.FavoritesRepository
import io.github.aakocval.petadopt.domain.repository.PetRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import javax.inject.Inject

/**
 * Joins pets with favorite state. Browse and Favorites both need this exact join, which is when
 * a use case earns its place: one tested implementation instead of two copies in ViewModels.
 *
 * `javax.inject` is a plain JSR-330 annotation, not an Android dependency.
 */
class ObservePetsWithFavoritesUseCase
    @Inject
    constructor(
        private val petRepository: PetRepository,
        private val favoritesRepository: FavoritesRepository,
    ) {
        operator fun invoke(): Flow<List<FavoritablePet>> =
            combine(petRepository.observePets(), favoritesRepository.observeFavoriteIds()) { pets, favoriteIds ->
                pets.map { FavoritablePet(pet = it, isFavorite = it.id in favoriteIds) }
            }
    }
