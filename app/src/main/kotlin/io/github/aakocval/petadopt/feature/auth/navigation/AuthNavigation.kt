package io.github.aakocval.petadopt.feature.auth.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import io.github.aakocval.petadopt.feature.auth.ui.LoginRoute
import kotlinx.serialization.Serializable

/** Root-level graph for signed-out users. Room to grow (sign up, reset password) without touching the root. */
@Serializable
data object AuthGraph

@Serializable
data object LoginDestination

/** No navigation lambdas: a successful sign-in changes the session and the app root switches graphs. */
fun NavGraphBuilder.authGraph() {
    navigation<AuthGraph>(startDestination = LoginDestination) {
        composable<LoginDestination> { LoginRoute() }
    }
}
