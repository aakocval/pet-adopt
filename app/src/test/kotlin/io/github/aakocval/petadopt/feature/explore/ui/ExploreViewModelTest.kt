package io.github.aakocval.petadopt.feature.explore.ui

import io.github.aakocval.petadopt.domain.model.GeoPoint
import io.github.aakocval.petadopt.testing.FakePetRepository
import io.github.aakocval.petadopt.testing.MainDispatcherExtension
import io.github.aakocval.petadopt.testing.testPet
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.RegisterExtension

class ExploreViewModelTest {
    @JvmField
    @RegisterExtension
    val mainDispatcher = MainDispatcherExtension()

    private val pets = FakePetRepository()
    private val rex =
        testPet(
            id = "dog-1",
            name = "Rex",
        ).copy(location = GeoPoint(37.79, -122.40), shelterName = "Harbor Paws")

    private fun TestScope.viewModel() =
        ExploreViewModel(pets).also { vm ->
            backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) { vm.uiState.collect() }
        }

    @Test
    fun `every pet becomes a pin at its location`() =
        runTest {
            val viewModel = viewModel()
            assertTrue(viewModel.uiState.value.isLoading)

            pets.send(listOf(rex, testPet(id = "cat-1")))

            val pin =
                viewModel.uiState.value.pins
                    .first()
            assertEquals("dog-1", pin.id)
            assertEquals(GeoPoint(37.79, -122.40), pin.location)
            assertEquals("Rex", pin.label)
            assertEquals(2, viewModel.uiState.value.pins.size)
        }

    @Test
    fun `selecting a pin shows its card and an empty tap hides it`() =
        runTest {
            val viewModel = viewModel()
            pets.send(listOf(rex))

            viewModel.onPinSelected("dog-1")
            assertEquals(
                "Harbor Paws",
                viewModel.uiState.value.selected
                    ?.shelterName,
            )

            viewModel.onPinSelected(null)
            assertNull(viewModel.uiState.value.selected)
        }

    @Test
    fun `a selected pet that disappears clears the card`() =
        runTest {
            val viewModel = viewModel()
            pets.send(listOf(rex))
            viewModel.onPinSelected("dog-1")

            pets.send(emptyList())

            assertNull(viewModel.uiState.value.selected)
        }
}
