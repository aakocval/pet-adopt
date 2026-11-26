package io.github.aakocval.petadopt.feature.explore.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import io.github.aakocval.petadopt.feature.explore.ui.ExploreScreen
import kotlinx.serialization.Serializable

@Serializable
data object ExploreGraph

@Serializable
data object ExploreDestination

fun NavController.navigateToExplore(navOptions: NavOptions? = null) = navigate(ExploreGraph, navOptions)

fun NavGraphBuilder.exploreGraph() {
    navigation<ExploreGraph>(startDestination = ExploreDestination) {
        composable<ExploreDestination> { ExploreScreen() }
    }
}
