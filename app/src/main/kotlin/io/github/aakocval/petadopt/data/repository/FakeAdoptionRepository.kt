package io.github.aakocval.petadopt.data.repository

import io.github.aakocval.petadopt.domain.model.AdoptionApplication
import io.github.aakocval.petadopt.domain.repository.AdoptionRepository
import io.github.aakocval.petadopt.domain.repository.SubmitResult
import kotlinx.coroutines.delay
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.random.Random

/**
 * Pretends to send the application to a shelter. Nothing leaves the device: applications are kept
 * in memory and a reference like "PA-7F3K2Q" comes back after a short simulated delay.
 */
@Singleton
class FakeAdoptionRepository internal constructor(
    private val random: Random,
) : AdoptionRepository {
    @Inject
    constructor() : this(Random.Default)

    private val submitted = mutableListOf<AdoptionApplication>()

    /** What has been "sent", for tests and debugging. */
    val submittedApplications: List<AdoptionApplication> get() = submitted.toList()

    override suspend fun submit(application: AdoptionApplication): SubmitResult {
        delay(SIMULATED_LATENCY_MILLIS)
        submitted += application
        return SubmitResult.Success(reference = newReference())
    }

    private fun newReference(): String =
        "PA-" +
            (1..REFERENCE_LENGTH).map { REFERENCE_ALPHABET[random.nextInt(REFERENCE_ALPHABET.length)] }.joinToString("")

    private companion object {
        const val SIMULATED_LATENCY_MILLIS = 800L
        const val REFERENCE_LENGTH = 6

        // No 0/O or 1/I: references get read out over the phone.
        const val REFERENCE_ALPHABET = "23456789ABCDEFGHJKLMNPQRSTUVWXYZ"
    }
}
