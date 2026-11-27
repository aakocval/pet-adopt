package io.github.aakocval.petadopt.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.github.aakocval.petadopt.feature.explore.map.CanvasMapProvider
import io.github.aakocval.petadopt.feature.explore.map.MapProvider

/** Swap the map here (e.g. a GoogleMapProvider) without touching the Explore screen. See docs/MAP.md. */
@Module
@InstallIn(SingletonComponent::class)
interface MapModule {
    @Binds
    fun bindsMapProvider(impl: CanvasMapProvider): MapProvider
}
