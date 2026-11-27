package io.github.aakocval.petadopt.data.remote

import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource

class RemotePhotoSourceTest {
    private val requestedBreeds = mutableListOf<String>()
    private var dogResponse =
        DogImageResponse(message = "https://images.dog.ceo/breeds/beagle/1.jpg", status = "success")
    private var catIds = listOf("abc123", "04eEQhDfAL8l5nt3")

    private val dogApi =
        object : DogCeoApi {
            override suspend fun randomImage(breed: String): DogImageResponse =
                dogResponse.also { requestedBreeds += breed }
        }
    private val catApi =
        object : CataasApi {
            override suspend fun cats(limit: Int): List<CatEntry> = catIds.take(limit).map(::CatEntry)
        }
    private val source = RemotePhotoSource(dogApi, catApi)

    @Test
    fun `returns the dog photo for a valid breed path`() =
        runTest {
            assertEquals("https://images.dog.ceo/breeds/beagle/1.jpg", source.dogPhoto("collie/border"))
            assertEquals(listOf("collie/border"), requestedBreeds)
        }

    @ParameterizedTest
    @ValueSource(strings = ["../../admin", "beagle?x=1", "Beagle", "a/b/c", "", "collie%2Fborder"])
    fun `never sends a suspicious breed path`(path: String) =
        runTest {
            assertNull(source.dogPhoto(path))
            assertEquals(emptyList<String>(), requestedBreeds)
        }

    @Test
    fun `rejects photos from unexpected hosts or schemes`() =
        runTest {
            dogResponse = DogImageResponse(message = "http://images.dog.ceo/breeds/beagle/1.jpg", status = "success")
            assertNull(source.dogPhoto("beagle"))

            dogResponse = DogImageResponse(message = "https://evil.example/cat.jpg", status = "success")
            assertNull(source.dogPhoto("beagle"))
        }

    @Test
    fun `rejects an error status`() =
        runTest {
            dogResponse = DogImageResponse(message = "Breed not found", status = "error")
            assertNull(source.dogPhoto("beagle"))
        }

    @Test
    fun `builds sized cat URLs and drops malformed ids`() =
        runTest {
            catIds = listOf("abc123", "../etc", "ok-id")
            assertEquals(
                listOf("https://cataas.com/cat/abc123?width=800", "https://cataas.com/cat/ok-id?width=800"),
                source.catPhotos(count = 10),
            )
        }
}
