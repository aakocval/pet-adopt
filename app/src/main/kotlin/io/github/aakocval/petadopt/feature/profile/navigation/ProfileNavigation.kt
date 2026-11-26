package io.github.aakocval.petadopt.feature.profile.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import io.github.aakocval.petadopt.feature.profile.ui.ProfileRoute
import kotlinx.serialization.Serializable

@Serializable
data object ProfileGraph

@Serializable
data object ProfileDestination

fun NavController.navigateToProfile(navOptions: NavOptions? = null) = navigate(ProfileGraph, navOptions)

/** @param onOpenFavorites cross-tab jump; the app decides it means "switch to the Favorites tab". */
fun NavGraphBuilder.profileGraph(onOpenFavorites: () -> Unit) {
    navigation<ProfileGraph>(startDestination = ProfileDestination) {
        composable<ProfileDestination> { ProfileRoute(onOpenFavorites = onOpenFavorites) }
    }
}
