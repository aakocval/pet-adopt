package io.github.aakocval.petadopt.feature.auth.ui

import io.github.aakocval.petadopt.domain.model.User
import io.github.aakocval.petadopt.domain.repository.AuthRepository
import io.github.aakocval.petadopt.domain.repository.SignInResult
import io.github.aakocval.petadopt.testing.MainDispatcherExtension
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.CompletableDeferred
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.RegisterExtension

class LoginViewModelTest {
    @JvmField
    @RegisterExtension
    val mainDispatcher = MainDispatcherExtension()

    private val user = User("Alex", "alex@example.com")
    private val auth = mockk<AuthRepository> { coEvery { signIn(any(), any()) } returns SignInResult.Success(user) }
    private val viewModel = LoginViewModel(auth)

    private fun fill(
        email: String = "alex@example.com",
        password: String = "longenough",
    ) {
        viewModel.onEmailChange(email)
        viewModel.onPasswordChange(password)
    }

    @Test
    fun `empty form shows both blank errors and does not call the backend`() {
        viewModel.onSubmit()

        assertEquals(EmailError.Blank, viewModel.uiState.value.emailError)
        assertEquals(PasswordError.Blank, viewModel.uiState.value.passwordError)
        coVerify(exactly = 0) { auth.signIn(any(), any()) }
    }

    @Test
    fun `malformed email and short password are rejected locally`() {
        fill(email = "alex@", password = "short")
        viewModel.onSubmit()

        assertEquals(EmailError.Invalid, viewModel.uiState.value.emailError)
        assertEquals(PasswordError.TooShort, viewModel.uiState.value.passwordError)
    }

    @Test
    fun `editing a field clears its error`() {
        viewModel.onSubmit()
        viewModel.onEmailChange("a")
        assertEquals(null, viewModel.uiState.value.emailError)
        assertEquals(PasswordError.Blank, viewModel.uiState.value.passwordError)
    }

    @Test
    fun `whitespace is stripped and length is capped`() {
        viewModel.onEmailChange("  alex @example.com \n")
        assertEquals("alex@example.com", viewModel.uiState.value.email)

        viewModel.onPasswordChange("x".repeat(1_000))
        assertEquals(128, viewModel.uiState.value.password.length)
    }

    @Test
    fun `valid form signs in and clears the password`() {
        fill()
        viewModel.onSubmit()

        coVerify(exactly = 1) { auth.signIn("alex@example.com", "longenough") }
        assertFalse(viewModel.uiState.value.isSubmitting)
        assertEquals("", viewModel.uiState.value.password)
    }

    @Test
    fun `rejected credentials show an error and keep the input`() {
        coEvery { auth.signIn(any(), any()) } returns SignInResult.InvalidCredentials
        fill()
        viewModel.onSubmit()

        assertTrue(viewModel.uiState.value.signInFailed)
        assertEquals("longenough", viewModel.uiState.value.password)
    }

    @Test
    fun `a second tap while submitting is ignored`() {
        val pending = CompletableDeferred<SignInResult>()
        coEvery { auth.signIn(any(), any()) } coAnswers { pending.await() }
        fill()

        viewModel.onSubmit()
        assertTrue(viewModel.uiState.value.isSubmitting)
        viewModel.onSubmit()
        pending.complete(SignInResult.Success(user))

        coVerify(exactly = 1) { auth.signIn(any(), any()) }
        assertFalse(viewModel.uiState.value.isSubmitting)
    }
}
