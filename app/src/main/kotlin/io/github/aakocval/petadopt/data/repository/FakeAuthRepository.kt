package io.github.aakocval.petadopt.data.repository

import io.github.aakocval.petadopt.data.local.FakeTokens
import io.github.aakocval.petadopt.data.local.SecureSessionStore
import io.github.aakocval.petadopt.data.local.StoredSession
import io.github.aakocval.petadopt.domain.model.User
import io.github.aakocval.petadopt.domain.repository.AuthRepository
import io.github.aakocval.petadopt.domain.repository.SignInResult
import io.github.aakocval.petadopt.domain.validation.PasswordPolicy
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Offline stand-in for a real auth backend: any well-formed email with a password of at least
 * [PasswordPolicy.MIN_LENGTH] characters signs in, after a short simulated network delay.
 *
 * Like a real backend it issues an access token. The token, email and name are stored only
 * encrypted with a Keystore-backed key ([SecureSessionStore]); the password is never stored.
 */
@Singleton
class FakeAuthRepository
    @Inject
    constructor(
        private val sessionStore: SecureSessionStore,
    ) : AuthRepository {
        override val currentUser: Flow<User?> =
            sessionStore.session.map { session ->
                session?.let { User(displayName = it.displayName, email = it.email) }
            }

        override suspend fun signIn(
            email: String,
            password: String,
        ): SignInResult {
            delay(SIMULATED_LATENCY_MILLIS)
            if (!PasswordPolicy.isLongEnough(password) || !email.contains('@')) return SignInResult.InvalidCredentials
            val user = User(displayName = displayNameFor(email), email = email)
            sessionStore.save(
                StoredSession(displayName = user.displayName, email = user.email, accessToken = FakeTokens.newToken()),
            )
            return SignInResult.Success(user)
        }

        override suspend fun signOut() {
            sessionStore.clear()
        }

        companion object {
            private const val SIMULATED_LATENCY_MILLIS = 600L

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
