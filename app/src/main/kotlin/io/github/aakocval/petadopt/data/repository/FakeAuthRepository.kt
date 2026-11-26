package io.github.aakocval.petadopt.data.repository

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.stringPreferencesKey
import io.github.aakocval.petadopt.domain.model.User
import io.github.aakocval.petadopt.domain.repository.AuthRepository
import io.github.aakocval.petadopt.domain.repository.SignInResult
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Offline stand-in for a real auth backend: any well-formed email with a password of at least
 * [MIN_PASSWORD_LENGTH] characters signs in, after a short simulated network delay.
 *
 * Only the display name and email are persisted, so the session survives restarts. No password
 * or token is ever stored. M7 moves session data into Keystore-backed encrypted storage.
 */
@Singleton
class FakeAuthRepository
    @Inject
    constructor(
        private val dataStore: DataStore<Preferences>,
    ) : AuthRepository {
        override val currentUser: Flow<User?> =
            dataStore.data
                .catch { error -> if (error is IOException) emit(emptyPreferences()) else throw error }
                .map { prefs ->
                    val email = prefs[SessionEmail] ?: return@map null
                    User(displayName = prefs[SessionName] ?: displayNameFor(email), email = email)
                }.distinctUntilChanged()

        override suspend fun signIn(
            email: String,
            password: String,
        ): SignInResult {
            delay(SIMULATED_LATENCY_MILLIS)
            if (password.length < MIN_PASSWORD_LENGTH || !email.contains('@')) return SignInResult.InvalidCredentials
            val user = User(displayName = displayNameFor(email), email = email)
            dataStore.edit {
                it[SessionEmail] = user.email
                it[SessionName] = user.displayName
            }
            return SignInResult.Success(user)
        }

        override suspend fun signOut() {
            dataStore.edit {
                it.remove(SessionEmail)
                it.remove(SessionName)
            }
        }

        companion object {
            const val MIN_PASSWORD_LENGTH = 8
            private const val SIMULATED_LATENCY_MILLIS = 600L
            private val SessionEmail = stringPreferencesKey("session_email")
            private val SessionName = stringPreferencesKey("session_display_name")

            /** "alex.morgan@example.com" -> "Alex". */
            internal fun displayNameFor(email: String): String =
                email
                    .substringBefore('@')
                    .split('.', '_', '-', '+')
                    .firstOrNull { it.isNotBlank() }
                    ?.replaceFirstChar { it.uppercase() }
                    ?: email
        }
    }
