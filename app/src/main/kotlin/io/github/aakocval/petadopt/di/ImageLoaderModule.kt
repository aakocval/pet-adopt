package io.github.aakocval.petadopt.di

import android.content.Context
import coil3.ImageLoader
import coil3.disk.DiskCache
import coil3.memory.MemoryCache
import coil3.network.okhttp.OkHttpNetworkFetcherFactory
import coil3.request.crossfade
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okio.Path.Companion.toOkioPath
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ImageLoaderModule {
    private const val MEMORY_CACHE_PERCENT = 0.20
    private const val DISK_CACHE_BYTES = 50L * 1024 * 1024

    /**
     * The app's single Coil loader:
     * - shares the app's OkHttpClient (one connection pool, one place for security config)
     * - memory cache for instant re-display while scrolling
     * - disk cache so photos seen once still show offline
     * - crossfade so images fade in over the illustrated placeholder
     */
    @Provides
    @Singleton
    fun providesImageLoader(
        @ApplicationContext context: Context,
        okHttpClient: dagger.Lazy<OkHttpClient>,
    ): ImageLoader =
        ImageLoader
            .Builder(context)
            .components { add(OkHttpNetworkFetcherFactory(callFactory = { okHttpClient.get() })) }
            .memoryCache { MemoryCache.Builder().maxSizePercent(context, MEMORY_CACHE_PERCENT).build() }
            .diskCache {
                DiskCache
                    .Builder()
                    .directory(
                        context.cacheDir.resolve("image_cache").toOkioPath(),
                    ).maxSizeBytes(DISK_CACHE_BYTES)
                    .build()
            }.crossfade(true)
            .build()
}
