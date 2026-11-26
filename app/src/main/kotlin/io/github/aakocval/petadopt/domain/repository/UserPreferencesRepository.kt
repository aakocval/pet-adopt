package io.github.aakocval.petadopt.domain.repository

import kotlinx.coroutines.flow.Flow

/** Small, non-sensitive app preferences. */
interface UserPreferencesRepository {
    val hasCompletedOnboarding: Flow<Boolean>

    suspend fun setOnboardingCompleted()
}
