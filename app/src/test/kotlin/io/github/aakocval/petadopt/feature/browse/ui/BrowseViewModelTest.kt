package io.github.aakocval.petadopt.feature.browse.ui

import app.cash.turbine.test
import io.github.aakocval.petadopt.data.repository.InMemoryFavoritesRepository
import io.github.aakocval.petadopt.domain.model.Species
import io.github.aakocval.petadopt.domain.repository.FavoritesRepository
import io.github.aakocval.petadopt.domain.usecase.ObservePetsWithFavoritesUseCase
import io.github.aakocval.petadopt.testing.FakePetRepository
import io.github.aakocval.petadopt.testing.MainDispatcherExtension
import io.github.aakocval.petadopt.testing.testPet
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.RegisterExtension

class BrowseViewModelTest {
    @JvmField
    @RegisterExtension
    val mainDispatcher = MainDispatcherExtension()

    private val dog = testPet(id = "dog-1", name = "Rex", species = Species.Dog)
    private val cat = testPet(id = "cat-1", name = "Mia", species = Species.Cat)
    private val pets = FakePetRepository()
    private val favorites = InMemoryFavoritesRepository()

    /**
     * Creates the ViewModel and keeps `uiState` subscribed for the whole test (as the UI would),
     * so assertions can read `uiState.value` deterministically.
     */
    private fun TestScope.viewModel(favoritesRepository: FavoritesRepository = favorites): BrowseViewModel {
        val viewModel = BrowseViewModel(ObservePetsWithFavoritesUseCase(pets, favoritesRepository), favoritesRepository)
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) { viewModel.uiState.collect() }
        return viewModel
    }

    private val BrowseViewModel.petIds get() = (uiState.value.content as BrowseContent.Pets).pets.map { it.id }

    @Test
    fun `is loading until pets arrive, then shows all of them`() =
        runTest {
            val viewModel = viewModel()
            assertEquals(BrowseContent.Loading, viewModel.uiState.value.content)

            pets.send(listOf(dog, cat))
            assertEquals(listOf("dog-1", "cat-1"), viewModel.petIds)
        }

    @Test
    fun `filter narrows the list and is reflected in state`() =
        runTest {
            val viewModel = viewModel()
            pets.send(listOf(dog, cat))

            viewModel.onFilterSelected(SpeciesFilter.Cats)

            assertEquals(SpeciesFilter.Cats, viewModel.uiState.value.filter)
            assertEquals(listOf("cat-1"), viewModel.petIds)
        }

    @Test
    fun `repository updates flow straight into state`() =
        runTest {
            val viewModel = viewModel()
            pets.send(listOf(dog, cat))

            pets.send(listOf(dog))

            assertEquals(listOf("dog-1"), viewModel.petIds)
        }

    @Test
    fun `toggling a favorite updates the tile and emits a one-off event`() =
        runTest {
            val viewModel = viewModel()
            pets.send(listOf(dog, cat))

            viewModel.events.test {
                viewModel.onToggleFavorite("dog-1", isFavorite = true)
                assertEquals(BrowseEvent.FavoriteChanged(petName = "Rex", isFavorite = true), awaitItem())
            }
            val rex = (viewModel.uiState.value.content as BrowseContent.Pets).pets.first { it.id == "dog-1" }
            assertTrue(rex.isFavorite)
        }

    @Test
    fun `toggling a favorite writes through the repository`() =
        runTest {
            // MockK for pure interaction checks; fakes for everything stateful.
            val favoritesMock =
                mockk<FavoritesRepository> {
                    every { observeFavoriteIds() } returns flowOf(emptySet())
                    coEvery { setFavorite(any(), any()) } returns Unit
                }
            val viewModel = viewModel(favoritesMock)
            pets.send(listOf(dog, cat))

            viewModel.onToggleFavorite("cat-1", isFavorite = true)

            coVerify(exactly = 1) { favoritesMock.setFavorite("cat-1", true) }
        }

    @Test
    fun `toggling an unknown pet does nothing`() =
        runTest {
            val favoritesMock = mockk<FavoritesRepository> { every { observeFavoriteIds() } returns flowOf(emptySet()) }
            val viewModel = viewModel(favoritesMock)
            pets.send(listOf(dog))

            viewModel.onToggleFavorite("missing", isFavorite = true)

            coVerify(exactly = 0) { favoritesMock.setFavorite(any(), any()) }
        }

    @Test
    fun `a failure shows the error state and retry recovers`() =
        runTest {
            pets.failNextSubscription = true
            pets.send(listOf(dog, cat))
            val viewModel = viewModel()
            assertEquals(BrowseContent.Error, viewModel.uiState.value.content)

            viewModel.onRetry()

            assertEquals(listOf("dog-1", "cat-1"), viewModel.petIds)
        }
}
