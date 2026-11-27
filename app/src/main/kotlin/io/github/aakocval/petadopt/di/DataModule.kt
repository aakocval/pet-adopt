package io.github.aakocval.petadopt.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.github.aakocval.petadopt.data.local.AssetPetDataSource
import io.github.aakocval.petadopt.data.local.PetCatalogSource
import io.github.aakocval.petadopt.data.repository.DataStoreUserPreferencesRepository
import io.github.aakocval.petadopt.data.repository.FakeAdoptionRepository
import io.github.aakocval.petadopt.data.repository.FakeAuthRepository
import io.github.aakocval.petadopt.data.repository.InMemoryFavoritesRepository
import io.github.aakocval.petadopt.data.repository.OfflineFirstPetRepository
import io.github.aakocval.petadopt.domain.repository.AdoptionRepository
import io.github.aakocval.petadopt.domain.repository.AuthRepository
import io.github.aakocval.petadopt.domain.repository.FavoritesRepository
import io.github.aakocval.petadopt.domain.repository.PetRepository
import io.github.aakocval.petadopt.domain.repository.UserPreferencesRepository

/** Binds domain interfaces to data implementations. Swapping an implementation is a one-line change here. */
@Module
@InstallIn(SingletonComponent::class)
interface DataModule {
    @Binds
    fun bindsPetCatalogSource(impl: AssetPetDataSource): PetCatalogSource

    @Binds
    fun bindsPetRepository(impl: OfflineFirstPetRepository): PetRepository

    @Binds
    fun bindsFavoritesRepository(impl: InMemoryFavoritesRepository): FavoritesRepository

    @Binds
    fun bindsUserPreferencesRepository(impl: DataStoreUserPreferencesRepository): UserPreferencesRepository

    @Binds
    fun bindsAuthRepository(impl: FakeAuthRepository): AuthRepository

    @Binds
    fun bindsAdoptionRepository(impl: FakeAdoptionRepository): AdoptionRepository
}
