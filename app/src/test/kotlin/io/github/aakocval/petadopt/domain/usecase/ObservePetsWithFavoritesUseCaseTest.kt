package io.github.aakocval.petadopt.domain.usecase

import app.cash.turbine.test
import io.github.aakocval.petadopt.data.repository.InMemoryFavoritesRepository
import io.github.aakocval.petadopt.domain.model.FavoritablePet
import io.github.aakocval.petadopt.testing.FakePetRepository
import io.github.aakocval.petadopt.testing.testPet
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class ObservePetsWithFavoritesUseCaseTest {
    private val rex = testPet(id = "dog-1")
    private val mia = testPet(id = "cat-1")
    private val pets = FakePetRepository()
    private val favorites = InMemoryFavoritesRepository()
    private val useCase = ObservePetsWithFavoritesUseCase(pets, favorites)

    @Test
    fun `marks favorited pets and keeps order`() =
        runTest {
            favorites.setFavorite("cat-1", isFavorite = true)
            pets.send(listOf(rex, mia))

            useCase().test {
                assertEquals(listOf(FavoritablePet(rex, false), FavoritablePet(mia, true)), awaitItem())
            }
        }

    @Test
    fun `re-emits when favorites change`() =
        runTest {
            pets.send(listOf(rex))
            useCase().test {
                assertEquals(false, awaitItem().single().isFavorite)
                favorites.setFavorite("dog-1", isFavorite = true)
                assertEquals(true, awaitItem().single().isFavorite)
            }
        }

    @Test
    fun `favorites for pets that no longer exist are ignored`() =
        runTest {
            favorites.setFavorite("gone", isFavorite = true)
            pets.send(listOf(rex))
            useCase().test {
                assertEquals(listOf(FavoritablePet(rex, false)), awaitItem())
            }
        }
}
