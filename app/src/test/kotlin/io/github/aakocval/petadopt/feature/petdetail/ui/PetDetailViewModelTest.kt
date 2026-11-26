package io.github.aakocval.petadopt.feature.petdetail.ui

import io.github.aakocval.petadopt.data.repository.InMemoryFavoritesRepository
import io.github.aakocval.petadopt.testing.FakePetRepository
import io.github.aakocval.petadopt.testing.MainDispatcherExtension
import io.github.aakocval.petadopt.testing.testPet
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.RegisterExtension

class PetDetailViewModelTest {
    @JvmField
    @RegisterExtension
    val mainDispatcher = MainDispatcherExtension()

    private val rex = testPet(id = "dog-1", name = "Rex")
    private val pets = FakePetRepository()
    private val favorites = InMemoryFavoritesRepository()

    private fun TestScope.viewModel(petId: String) =
        PetDetailViewModel(petId, pets, favorites).also { vm ->
            backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) { vm.uiState.collect() }
        }

    @Test
    fun `loads the pet for the given id`() =
        runTest {
            val viewModel = viewModel("dog-1")
            assertEquals(PetDetailUiState.Loading, viewModel.uiState.value)

            pets.send(listOf(rex))

            val state = viewModel.uiState.value as PetDetailUiState.Success
            assertEquals("Rex", state.pet.name)
        }

    @Test
    fun `unknown id shows not found instead of crashing`() =
        runTest {
            val viewModel = viewModel("missing")
            pets.send(listOf(rex))
            assertEquals(PetDetailUiState.NotFound, viewModel.uiState.value)
        }

    @Test
    fun `toggling favorite is reflected in state`() =
        runTest {
            val viewModel = viewModel("dog-1")
            pets.send(listOf(rex))

            viewModel.onToggleFavorite(isFavorite = true)

            assertTrue((viewModel.uiState.value as PetDetailUiState.Success).pet.isFavorite)
        }

    @Test
    fun `pet removed while open switches to not found`() =
        runTest {
            val viewModel = viewModel("dog-1")
            pets.send(listOf(rex))

            pets.send(emptyList())

            assertEquals(PetDetailUiState.NotFound, viewModel.uiState.value)
        }
}
