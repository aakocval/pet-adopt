package io.github.aakocval.petadopt.data.repository

import app.cash.turbine.test
import io.github.aakocval.petadopt.domain.model.Species
import io.github.aakocval.petadopt.testing.testPet
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class InMemoryPetRepositoryTest {
    private val dog = testPet(id = "dog-1", species = Species.Dog)
    private val cat = testPet(id = "cat-1", species = Species.Cat)
    private val repository = InMemoryPetRepository(seed = listOf(dog, cat))

    @Test
    fun `observePets emits the seed`() =
        runTest {
            repository.observePets().test {
                assertEquals(listOf(dog, cat), awaitItem())
            }
        }

    @Test
    fun `observePet finds a pet by id`() =
        runTest {
            repository.observePet("cat-1").test {
                assertEquals(cat, awaitItem())
            }
        }

    @Test
    fun `observePet emits null for an unknown id`() =
        runTest {
            repository.observePet("missing").test {
                assertNull(awaitItem())
            }
        }

    @Test
    fun `default seed has unique ids and both species`() =
        runTest {
            InMemoryPetRepository().observePets().test {
                val pets = awaitItem()
                assertEquals(pets.size, pets.map { it.id }.toSet().size, "duplicate pet ids in MockPets")
                assertTrue(pets.any { it.species == Species.Dog } && pets.any { it.species == Species.Cat })
            }
        }
}
