package io.github.aakocval.petadopt.domain.repository

import io.github.aakocval.petadopt.domain.model.AdoptionApplication

interface AdoptionRepository {
    suspend fun submit(application: AdoptionApplication): SubmitResult
}

sealed interface SubmitResult {
    /** [reference] is what the shelter quotes back, e.g. "PA-7F3K2Q". */
    data class Success(
        val reference: String,
    ) : SubmitResult

    data object Failure : SubmitResult
}
