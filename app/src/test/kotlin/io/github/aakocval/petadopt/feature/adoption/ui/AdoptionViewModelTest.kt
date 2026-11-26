package io.github.aakocval.petadopt.feature.adoption.ui

import androidx.lifecycle.SavedStateHandle
import app.cash.turbine.test
import io.github.aakocval.petadopt.domain.model.HomeType
import io.github.aakocval.petadopt.domain.model.User
import io.github.aakocval.petadopt.domain.repository.AdoptionRepository
import io.github.aakocval.petadopt.domain.repository.AuthRepository
import io.github.aakocval.petadopt.domain.repository.SubmitResult
import io.github.aakocval.petadopt.domain.validation.ApplicationField
import io.github.aakocval.petadopt.domain.validation.FieldError
import io.github.aakocval.petadopt.testing.FakePetRepository
import io.github.aakocval.petadopt.testing.MainDispatcherExtension
import io.github.aakocval.petadopt.testing.testPet
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.RegisterExtension

class AdoptionViewModelTest {
    @JvmField
    @RegisterExtension
    val mainDispatcher = MainDispatcherExtension()

    private val pets = FakePetRepository()
    private val auth = mockk<AuthRepository> { every { currentUser } returns flowOf(User("Alex", "alex@example.com")) }
    private val adoption =
        mockk<AdoptionRepository> {
            coEvery { submit(any()) } returns
                SubmitResult.Success("PA-ABC234")
        }

    private fun TestScope.viewModel(handle: SavedStateHandle = SavedStateHandle()) =
        AdoptionViewModel("dog-1", handle, pets, auth, adoption).also { vm ->
            backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) { vm.uiState.collect() }
        }

    private fun AdoptionViewModel.fillValidForm() {
        onPhoneChange("+1 555 010 2030")
        onHomeTypeSelect(HomeType.Apartment)
        onMessageChange("Quiet flat near a big park, home all day.")
        onHomeVisitChange(true)
    }

    @Test
    fun `shows the pet name and prefills the account details`() =
        runTest {
            val viewModel = viewModel()
            pets.send(listOf(testPet(id = "dog-1", name = "Rex")))

            val state = viewModel.uiState.value
            assertEquals("Rex", state.petName)
            assertEquals("Alex", state.draft.fullName)
            assertEquals("alex@example.com", state.draft.email)
        }

    @Test
    fun `errors stay hidden until the user tries to continue, then update live`() =
        runTest {
            val viewModel = viewModel()
            assertTrue(
                viewModel.uiState.value.errors
                    .isEmpty(),
            )

            viewModel.events.test {
                viewModel.onContinue()
                expectNoEvents()
            }
            assertEquals(FieldError.Required, viewModel.uiState.value.errors[ApplicationField.Phone])

            viewModel.onPhoneChange("+1 555 010 2030")
            assertFalse(ApplicationField.Phone in viewModel.uiState.value.errors)
        }

    @Test
    fun `a valid form moves on to review`() =
        runTest {
            val viewModel = viewModel()
            viewModel.fillValidForm()

            viewModel.events.test {
                viewModel.onContinue()
                assertEquals(AdoptionEvent.ShowReview, awaitItem())
            }
        }

    @Test
    fun `input is sanitized as it is typed`() =
        runTest {
            val viewModel = viewModel()
            viewModel.onPhoneChange("555-0102 call me!")
            viewModel.onMessageChange("Hello\u0000 there")

            assertEquals("555-0102  ", viewModel.uiState.value.draft.phone)
            assertEquals("Hello there", viewModel.uiState.value.draft.message)
        }

    @Test
    fun `the draft survives process death`() =
        runTest {
            val handle = SavedStateHandle()
            viewModel(handle).fillValidForm()

            // A new ViewModel built from the same saved state, as after the OS kills the app.
            val restored = viewModel(handle)

            assertEquals(HomeType.Apartment, restored.uiState.value.draft.homeType)
            assertEquals("Quiet flat near a big park, home all day.", restored.uiState.value.draft.message)
            assertTrue(restored.uiState.value.draft.agreesToHomeVisit)
        }

    @Test
    fun `submitting sends a normalized application and reports the reference`() =
        runTest {
            val viewModel = viewModel()
            viewModel.fillValidForm()

            viewModel.events.test {
                viewModel.onSubmit()
                assertEquals(AdoptionEvent.Submitted, awaitItem())
            }
            assertEquals("PA-ABC234", viewModel.uiState.value.reference)
            coVerify { adoption.submit(match { it.petId == "dog-1" && it.phone == "+15550102030" }) }
        }

    @Test
    fun `an invalid draft is never submitted, even from review`() =
        runTest {
            val viewModel = viewModel()
            viewModel.onSubmit()
            coVerify(exactly = 0) { adoption.submit(any()) }
            assertTrue(
                viewModel.uiState.value.errors
                    .isNotEmpty(),
            )
        }

    @Test
    fun `a second submit while one is in flight is ignored`() =
        runTest {
            val pending = CompletableDeferred<SubmitResult>()
            coEvery { adoption.submit(any()) } coAnswers { pending.await() }
            val viewModel = viewModel()
            viewModel.fillValidForm()

            viewModel.onSubmit()
            viewModel.onSubmit()
            pending.complete(SubmitResult.Success("PA-ABC234"))

            coVerify(exactly = 1) { adoption.submit(any()) }
        }

    @Test
    fun `a failed submit can be retried after editing`() =
        runTest {
            coEvery { adoption.submit(any()) } returns SubmitResult.Failure
            val viewModel = viewModel()
            viewModel.fillValidForm()

            viewModel.onSubmit()
            assertTrue(viewModel.uiState.value.submitFailed)

            viewModel.onMessageChange("Quiet flat near a big park, home all day!")
            assertFalse(viewModel.uiState.value.submitFailed)
        }
}
