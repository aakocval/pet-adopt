package io.github.aakocval.petadopt.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.github.aakocval.petadopt.data.repository.InMemoryFavoritesRepository
import io.github.aakocval.petadopt.data.repository.InMemoryPetRepository
import io.github.aakocval.petadopt.domain.repository.FavoritesRepository
import io.github.aakocval.petadopt.domain.repository.PetRepository

/** Binds domain interfaces to data implementations. Swapping an implementation is a one-line change here. */
@Module
@InstallIn(SingletonComponent::class)
interface DataModule {
    @Binds
    fun bindsPetRepository(impl: InMemoryPetRepository): PetRepository

    @Binds
    fun bindsFavoritesRepository(impl: InMemoryFavoritesRepository): FavoritesRepository
}
