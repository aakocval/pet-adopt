package io.github.aakocval.petadopt.data.repository

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import io.github.aakocval.petadopt.domain.repository.UserPreferencesRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import java.io.IOException
import javax.inject.Inject

class DataStoreUserPreferencesRepository
    @Inject
    constructor(
        private val dataStore: DataStore<Preferences>,
    ) : UserPreferencesRepository {
        override val hasCompletedOnboarding: Flow<Boolean> =
            dataStore.data
                // A corrupt or unreadable file falls back to defaults instead of crashing the app.
                .catch { error -> if (error is IOException) emit(emptyPreferences()) else throw error }
                .map { it[OnboardingCompleted] ?: false }
                .distinctUntilChanged()

        override suspend fun setOnboardingCompleted() {
            dataStore.edit { it[OnboardingCompleted] = true }
        }

        private companion object {
            val OnboardingCompleted = booleanPreferencesKey("onboarding_completed")
        }
    }
