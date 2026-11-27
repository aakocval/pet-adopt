package io.github.aakocval.petadopt.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.navigation.NavController
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import io.github.aakocval.petadopt.core.deeplink.DeepLink
import io.github.aakocval.petadopt.feature.auth.navigation.AuthGraph
import io.github.aakocval.petadopt.feature.auth.navigation.authGraph
import io.github.aakocval.petadopt.feature.onboarding.navigation.OnboardingGraph
import io.github.aakocval.petadopt.feature.onboarding.navigation.onboardingGraph
import io.github.aakocval.petadopt.ui.PetApp
import io.github.aakocval.petadopt.ui.RootDestination
import kotlinx.serialization.Serializable

/** The signed-in app (bottom tabs with their own nested NavHost). */
@Serializable
data object MainDestination

/** The route that represents each root destination. */
val RootDestination.route: Any
    get() =
        when (this) {
            RootDestination.Onboarding -> OnboardingGraph
            RootDestination.Auth -> AuthGraph
            RootDestination.Main -> MainDestination
        }

/**
 * Top-level host: Onboarding -> Auth -> Main.
 *
 * [destination] comes from `RootViewModel`. Whenever it changes, the whole back stack is replaced,
 * so after signing in Back can't return to login, and after signing out it can't return to the app.
 */
@Composable
fun RootNavHost(
    destination: RootDestination,
    pendingDeepLink: DeepLink? = null,
    onDeepLinkHandled: () -> Unit = {},
    navController: NavHostController = rememberNavController(),
) {
    // The start destination is fixed at first composition; later changes go through switchRootTo.
    val startDestination = remember { destination.route }
    NavHost(navController = navController, startDestination = startDestination) {
        onboardingGraph()
        authGraph()
        composable<MainDestination> { PetApp(pendingDeepLink = pendingDeepLink, onDeepLinkHandled = onDeepLinkHandled) }
    }
    LaunchedEffect(destination) { navController.switchRootTo(destination.route) }
}

/**
 * Shows [route] as the only entry on the back stack. A no-op if it's already showing, so
 * recompositions and configuration changes don't reset anything.
 */
fun NavController.switchRootTo(route: Any) {
    val alreadyThere = currentDestination?.hierarchy?.any { it.hasRoute(route::class) } == true
    if (alreadyThere) return
    navigate(route) {
        popUpTo(graph.id) { inclusive = true }
        launchSingleTop = true
    }
}
