package io.github.aakocval.petadopt.data.repository

import io.github.aakocval.petadopt.testing.testPreferencesDataStore
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir
import java.io.File

class DataStoreUserPreferencesRepositoryTest {
    @TempDir
    lateinit var directory: File

    @Test
    fun `onboarding starts incomplete and stays complete once set`() =
        runTest {
            val repository = DataStoreUserPreferencesRepository(testPreferencesDataStore(directory))
            assertFalse(repository.hasCompletedOnboarding.first())

            repository.setOnboardingCompleted()

            assertTrue(repository.hasCompletedOnboarding.first())
        }
}
