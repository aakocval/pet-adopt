package io.github.aakocval.petadopt.data.repository

import app.cash.turbine.test
import io.github.aakocval.petadopt.domain.model.User
import io.github.aakocval.petadopt.domain.repository.SignInResult
import io.github.aakocval.petadopt.testing.testPreferencesDataStore
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource
import java.io.File

class FakeAuthRepositoryTest {
    @TempDir
    lateinit var directory: File

    @Test
    fun `sign in persists the session and sign out clears it`() =
        runTest {
            val repository = FakeAuthRepository(testPreferencesDataStore(directory))
            repository.currentUser.test {
                assertNull(awaitItem())

                val result = repository.signIn("alex.morgan@example.com", "longenough")
                val expected = User(displayName = "Alex", email = "alex.morgan@example.com")
                assertEquals(SignInResult.Success(expected), result)
                assertEquals(expected, awaitItem())

                repository.signOut()
                assertNull(awaitItem())
            }
        }

    @Test
    fun `short password is rejected and nothing is stored`() =
        runTest {
            val repository = FakeAuthRepository(testPreferencesDataStore(directory))
            assertEquals(SignInResult.InvalidCredentials, repository.signIn("alex@example.com", "short"))
            assertNull(repository.currentUser.first())
        }

    @ParameterizedTest
    @CsvSource(
        "alex.morgan@example.com,Alex",
        "sam_lee@example.com,Sam",
        "jo@example.com,Jo",
        "@example.com,@example.com",
    )
    fun `display name comes from the email's first name part`(
        email: String,
        expected: String,
    ) {
        assertEquals(expected, FakeAuthRepository.displayNameFor(email))
    }
}
