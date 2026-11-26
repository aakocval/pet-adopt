package io.github.aakocval.petadopt.domain.repository

import io.github.aakocval.petadopt.domain.model.User
import kotlinx.coroutines.flow.Flow

/**
 * Who is signed in. The app's root observes [currentUser] and switches between the auth graph
 * and the main graph, so screens never navigate on sign-in or sign-out themselves.
 */
interface AuthRepository {
    /** The signed-in user, or `null` when signed out. */
    val currentUser: Flow<User?>

    suspend fun signIn(
        email: String,
        password: String,
    ): SignInResult

    suspend fun signOut()
}

sealed interface SignInResult {
    data class Success(
        val user: User,
    ) : SignInResult

    data object InvalidCredentials : SignInResult
}
