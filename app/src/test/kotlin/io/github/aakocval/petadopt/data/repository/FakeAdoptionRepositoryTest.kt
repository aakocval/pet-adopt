package io.github.aakocval.petadopt.data.repository

import io.github.aakocval.petadopt.domain.model.AdoptionApplication
import io.github.aakocval.petadopt.domain.model.HomeType
import io.github.aakocval.petadopt.domain.repository.SubmitResult
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import kotlin.random.Random

class FakeAdoptionRepositoryTest {
    private val application =
        AdoptionApplication(
            petId = "dog-1",
            fullName = "Alex Morgan",
            email = "alex@example.com",
            phone = "+15550102030",
            homeType = HomeType.House,
            hasOtherPets = false,
            message = "We have a big garden and love long walks.",
        )

    @Test
    fun `submitting records the application and returns a readable reference`() =
        runTest {
            val repository = FakeAdoptionRepository(Random(seed = 42))

            val result = repository.submit(application) as SubmitResult.Success

            assertTrue(result.reference.matches(Regex("PA-[2-9A-HJ-NP-Z]{6}")), result.reference)
            assertEquals(listOf(application), repository.submittedApplications)
        }
}
