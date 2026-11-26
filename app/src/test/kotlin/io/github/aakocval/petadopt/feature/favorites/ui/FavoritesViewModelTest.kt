package io.github.aakocval.petadopt.feature.favorites.ui

import io.github.aakocval.petadopt.data.repository.InMemoryFavoritesRepository
import io.github.aakocval.petadopt.domain.usecase.ObservePetsWithFavoritesUseCase
import io.github.aakocval.petadopt.testing.FakePetRepository
import io.github.aakocval.petadopt.testing.MainDispatcherExtension
import io.github.aakocval.petadopt.testing.testPet
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.RegisterExtension

class FavoritesViewModelTest {
    @JvmField
    @RegisterExtension
    val mainDispatcher = MainDispatcherExtension()

    private val pets = FakePetRepository()
    private val favorites = InMemoryFavoritesRepository()

    private fun TestScope.viewModel() =
        FavoritesViewModel(ObservePetsWithFavoritesUseCase(pets, favorites), favorites).also { vm ->
            backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) { vm.uiState.collect() }
        }

    private val FavoritesViewModel.petIds get() = (uiState.value as FavoritesUiState.Pets).pets.map { it.id }

    @Test
    fun `no favorites shows the empty state`() =
        runTest {
            val viewModel = viewModel()
            assertEquals(FavoritesUiState.Loading, viewModel.uiState.value)

            pets.send(listOf(testPet(id = "dog-1")))

            assertEquals(FavoritesUiState.Empty, viewModel.uiState.value)
        }

    @Test
    fun `shows only favorited pets, in catalog order`() =
        runTest {
            favorites.setFavorite("c", isFavorite = true)
            favorites.setFavorite("a", isFavorite = true)
            val viewModel = viewModel()

            pets.send(listOf(testPet(id = "a"), testPet(id = "b"), testPet(id = "c")))

            assertEquals(listOf("a", "c"), viewModel.petIds)
        }

    @Test
    fun `removing the last favorite returns to empty`() =
        runTest {
            favorites.setFavorite("a", isFavorite = true)
            val viewModel = viewModel()
            pets.send(listOf(testPet(id = "a")))

            viewModel.onRemoveFavorite("a")

            assertEquals(FavoritesUiState.Empty, viewModel.uiState.value)
        }
}
