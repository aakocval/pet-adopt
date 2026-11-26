package io.github.aakocval.petadopt.feature.onboarding.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import io.github.aakocval.petadopt.feature.onboarding.ui.OnboardingRoute
import kotlinx.serialization.Serializable

/** Root-level graph shown once, before the auth gate. */
@Serializable
data object OnboardingGraph

@Serializable
data object OnboardingDestination

/** No navigation lambdas: finishing updates a preference and the app root switches graphs. */
fun NavGraphBuilder.onboardingGraph() {
    navigation<OnboardingGraph>(startDestination = OnboardingDestination) {
        composable<OnboardingDestination> { OnboardingRoute() }
    }
}
