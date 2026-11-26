package io.github.aakocval.petadopt.feature.browse.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import io.github.aakocval.petadopt.feature.browse.ui.BrowseRoute
import kotlinx.serialization.Serializable

/** The Browse tab's nested graph. Each bottom tab owns one, which gives it its own back stack. */
@Serializable
data object BrowseGraph

/** The Browse list itself: the start destination of [BrowseGraph]. */
@Serializable
data object BrowseDestination

fun NavController.navigateToBrowse(navOptions: NavOptions? = null) = navigate(BrowseGraph, navOptions)

/**
 * Adds the Browse tab to a nav graph.
 *
 * The feature receives navigation as lambdas instead of a NavController, so it never imports
 * another feature: the app's nav host decides what "pet clicked" means.
 *
 * @param nestedGraphs destinations pushed on top of Browse within this tab (e.g. Pet Detail),
 * contributed by other features through the app's nav host.
 */
fun NavGraphBuilder.browseGraph(
    onPetClick: (petId: String) -> Unit,
    onShowSnackbar: suspend (message: String) -> Unit,
    nestedGraphs: NavGraphBuilder.() -> Unit = {},
) {
    navigation<BrowseGraph>(startDestination = BrowseDestination) {
        composable<BrowseDestination> {
            BrowseRoute(onPetClick = onPetClick, onShowSnackbar = onShowSnackbar)
        }
        nestedGraphs()
    }
}
