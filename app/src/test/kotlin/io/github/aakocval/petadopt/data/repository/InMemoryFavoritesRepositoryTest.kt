package io.github.aakocval.petadopt.data.repository

import app.cash.turbine.test
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class InMemoryFavoritesRepositoryTest {
    private val repository = InMemoryFavoritesRepository()

    @Test
    fun `starts empty, then adds and removes favorites`() =
        runTest {
            repository.observeFavoriteIds().test {
                assertEquals(emptySet<String>(), awaitItem())

                repository.setFavorite("dog-1", isFavorite = true)
                assertEquals(setOf("dog-1"), awaitItem())

                repository.setFavorite("cat-1", isFavorite = true)
                assertEquals(setOf("dog-1", "cat-1"), awaitItem())

                repository.setFavorite("dog-1", isFavorite = false)
                assertEquals(setOf("cat-1"), awaitItem())
            }
        }

    @Test
    fun `setting the same state twice does not emit again`() =
        runTest {
            repository.observeFavoriteIds().test {
                awaitItem()
                repository.setFavorite("dog-1", isFavorite = true)
                awaitItem()
                repository.setFavorite("dog-1", isFavorite = true)
                expectNoEvents()
            }
        }
}
