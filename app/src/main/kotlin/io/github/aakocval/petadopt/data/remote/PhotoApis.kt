package io.github.aakocval.petadopt.data.remote

import kotlinx.serialization.Serializable
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

/** https://dog.ceo/dog-api/ */
interface DogCeoApi {
    /**
     * [breed] may contain one slash for sub-breeds ("collie/border"), hence `encoded = true`.
     * Callers must validate it first (see RemotePhotoSource).
     */
    @GET("api/breed/{breed}/images/random")
    suspend fun randomImage(
        @Path("breed", encoded = true) breed: String,
    ): DogImageResponse
}

@Serializable
data class DogImageResponse(
    val message: String,
    val status: String,
)

/** https://cataas.com/ */
interface CataasApi {
    @GET("api/cats")
    suspend fun cats(
        @Query("limit") limit: Int,
    ): List<CatEntry>
}

@Serializable
data class CatEntry(
    val id: String,
)
