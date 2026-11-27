package io.github.aakocval.petadopt.data.repository

import app.cash.turbine.test
import io.github.aakocval.petadopt.core.logging.NoOpLogger
import io.github.aakocval.petadopt.data.local.PetCatalogSource
import io.github.aakocval.petadopt.data.local.PhotoUrlCache
import io.github.aakocval.petadopt.data.model.PetRecord
import io.github.aakocval.petadopt.data.model.SexRecord
import io.github.aakocval.petadopt.data.model.SpeciesRecord
import io.github.aakocval.petadopt.data.remote.CatEntry
import io.github.aakocval.petadopt.data.remote.CataasApi
import io.github.aakocval.petadopt.data.remote.DogCeoApi
import io.github.aakocval.petadopt.data.remote.DogImageResponse
import io.github.aakocval.petadopt.data.remote.RemotePhotoSource
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.json.Json
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir
import java.io.File
import java.io.IOException

class OfflineFirstPetRepositoryTest {
    @TempDir
    lateinit var directory: File

    private val dog = record("dog-1", SpeciesRecord.Dog, photoQuery = "beagle")
    private val cat = record("cat-1", SpeciesRecord.Cat)

    private var offline = false
    private var catalogLoads = 0
    private val dogGate = CompletableDeferred<Unit>().apply { complete(Unit) }

    private val dogApi =
        object : DogCeoApi {
            override suspend fun randomImage(breed: String): DogImageResponse {
                dogGate.await()
                if (offline) throw IOException("offline")
                return DogImageResponse("https://images.dog.ceo/breeds/$breed/1.jpg", "success")
            }
        }
    private val catApi =
        object : CataasApi {
            override suspend fun cats(limit: Int): List<CatEntry> {
                if (offline) throw IOException("offline")
                return listOf(CatEntry("cat123"))
            }
        }

    private fun TestScope.repository(
        gate: CompletableDeferred<Unit> = dogGate,
        cacheFile: File = File(directory, "urls.json"),
    ): OfflineFirstPetRepository {
        val gatedDogApi =
            object : DogCeoApi {
                override suspend fun randomImage(breed: String): DogImageResponse {
                    gate.await()
                    return dogApi.randomImage(breed)
                }
            }
        return OfflineFirstPetRepository(
            catalog =
                PetCatalogSource {
                    catalogLoads++
                    listOf(dog, cat)
                },
            photoCache = PhotoUrlCache(cacheFile, Json, UnconfinedTestDispatcher(testScheduler)),
            photos = RemotePhotoSource(gatedDogApi, catApi),
            ioDispatcher = UnconfinedTestDispatcher(testScheduler),
            appScope = backgroundScope,
            logger = NoOpLogger,
        )
    }

    @Test
    fun `shows the local catalog before the network answers, then adds photos`() =
        runTest {
            val slowNetwork = CompletableDeferred<Unit>()
            val repository = repository(gate = slowNetwork)

            repository.observePets().test {
                val first = awaitItem()
                assertEquals(listOf("dog-1", "cat-1"), first.map { it.id })
                assertTrue(first.all { it.photoUrl == null })

                slowNetwork.complete(Unit)
                val withPhotos = awaitItem()
                assertEquals(
                    "https://images.dog.ceo/breeds/beagle/1.jpg",
                    withPhotos.first { it.id == "dog-1" }.photoUrl,
                )
                assertEquals("https://cataas.com/cat/cat123?width=800", withPhotos.first { it.id == "cat-1" }.photoUrl)
            }
        }

    @Test
    fun `offline still shows every pet, just without photos`() =
        runTest {
            offline = true
            val repository = repository()

            repository.observePets().test {
                assertEquals(2, awaitItem().size)
                advanceUntilIdle()
                expectNoEvents()
            }
        }

    @Test
    fun `photos fetched once are there instantly on the next launch, even offline`() =
        runTest {
            repository().observePets().test {
                awaitItem()
                awaitItem() // photos fetched and cached
            }

            offline = true
            repository().observePets().test {
                val pets = awaitItem()
                assertTrue(pets.all { it.photoUrl != null }, "expected cached URLs, got $pets")
            }
        }

    @Test
    fun `a launch that starts offline heals once a later subscriber sees the network`() =
        runTest {
            offline = true
            val repository = repository()
            repository.observePets().test {
                awaitItem()
                advanceUntilIdle()
            }

            offline = false
            repository.observePets().test {
                assertTrue(awaitItem().all { it.photoUrl == null }, "still the offline state at first")
                assertTrue(awaitItem().all { it.photoUrl != null }, "photos after the retry")
            }
        }

    @Test
    fun `several subscribers share one catalog load`() =
        runTest {
            val repository = repository()
            repository.observePets().test { awaitItem() }
            repository.observePet("cat-1").test { assertEquals("cat-1", awaitItem()?.id) }
            assertEquals(1, catalogLoads)
        }

    private fun record(
        id: String,
        species: SpeciesRecord,
        photoQuery: String? = null,
    ) = PetRecord(
        id = id,
        name = id,
        species = species,
        breed = "Mixed",
        ageInMonths = 12,
        sex = SexRecord.Female,
        bio = "A good pet.",
        shelterName = "Test Shelter",
        latitude = 37.77,
        longitude = -122.42,
        photoQuery = photoQuery,
    )
}
