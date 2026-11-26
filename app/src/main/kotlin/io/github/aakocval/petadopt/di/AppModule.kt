package io.github.aakocval.petadopt.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.github.aakocval.petadopt.BuildConfig
import io.github.aakocval.petadopt.core.common.AppInfo

@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    @Provides
    fun providesAppInfo(): AppInfo = AppInfo(versionName = BuildConfig.VERSION_NAME)
}
