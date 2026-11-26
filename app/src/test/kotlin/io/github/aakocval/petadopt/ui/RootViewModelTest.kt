package io.github.aakocval.petadopt.ui

import io.github.aakocval.petadopt.domain.model.User
import io.github.aakocval.petadopt.domain.repository.AuthRepository
import io.github.aakocval.petadopt.domain.repository.SignInResult
import io.github.aakocval.petadopt.domain.repository.UserPreferencesRepository
import io.github.aakocval.petadopt.testing.MainDispatcherExtension
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.RegisterExtension
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource

class RootViewModelTest {
    @JvmField
    @RegisterExtension
    val mainDispatcher = MainDispatcherExtension()

    private val preferences =
        object : UserPreferencesRepository {
            override val hasCompletedOnboarding = MutableStateFlow(false)

            override suspend fun setOnboardingCompleted() {
                hasCompletedOnboarding.value = true
            }
        }

    private val auth =
        object : AuthRepository {
            override val currentUser = MutableStateFlow<User?>(null)

            override suspend fun signIn(
                email: String,
                password: String,
            ) = SignInResult.Success(User("Alex", email)).also { currentUser.value = it.user }

            override suspend fun signOut() {
                currentUser.value = null
            }
        }

    @ParameterizedTest
    @CsvSource("false,false,Onboarding", "false,true,Onboarding", "true,false,Auth", "true,true,Main")
    fun `maps onboarding and session to a root destination`(
        onboarded: Boolean,
        signedIn: Boolean,
        expected: RootDestination,
    ) {
        assertEquals(expected, rootDestinationFor(onboarded, signedIn))
    }

    @Test
    fun `walks the whole lifecycle - onboarding, sign in, sign out`() =
        runTest {
            val viewModel = RootViewModel(preferences, auth)
            assertEquals(RootUiState.Ready(RootDestination.Onboarding), viewModel.uiState.value)

            preferences.setOnboardingCompleted()
            assertEquals(RootUiState.Ready(RootDestination.Auth), viewModel.uiState.value)

            auth.signIn("alex@example.com", "longenough")
            assertEquals(RootUiState.Ready(RootDestination.Main), viewModel.uiState.value)

            auth.signOut()
            assertEquals(RootUiState.Ready(RootDestination.Auth), viewModel.uiState.value)
        }
}
