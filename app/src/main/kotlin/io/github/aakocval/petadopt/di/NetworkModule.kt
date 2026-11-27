package io.github.aakocval.petadopt.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.github.aakocval.petadopt.data.remote.CataasApi
import io.github.aakocval.petadopt.data.remote.DogCeoApi
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {
    private const val TIMEOUT_SECONDS = 10L

    /** One client for the whole app (Retrofit and Coil), so connections and caches are shared. */
    @Provides
    @Singleton
    fun providesOkHttpClient(): OkHttpClient =
        OkHttpClient
            .Builder()
            .connectTimeout(TIMEOUT_SECONDS, TimeUnit.SECONDS)
            .readTimeout(TIMEOUT_SECONDS, TimeUnit.SECONDS)
            .build()

    @Provides
    @Singleton
    fun providesDogCeoApi(
        client: OkHttpClient,
        json: Json,
    ): DogCeoApi = retrofit("https://dog.ceo/", client, json).create(DogCeoApi::class.java)

    @Provides
    @Singleton
    fun providesCataasApi(
        client: OkHttpClient,
        json: Json,
    ): CataasApi = retrofit("https://cataas.com/", client, json).create(CataasApi::class.java)

    private fun retrofit(
        baseUrl: String,
        client: OkHttpClient,
        json: Json,
    ): Retrofit =
        Retrofit
            .Builder()
            .baseUrl(baseUrl)
            // Lazy: building the client and services doesn't happen until a call is made.
            .callFactory { client.newCall(it) }
            .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
            .build()
}
