package io.github.aakocval.petadopt.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import io.github.aakocval.petadopt.feature.adoption.navigation.adoptionGraph
import io.github.aakocval.petadopt.feature.adoption.navigation.navigateToAdoption
import io.github.aakocval.petadopt.feature.browse.navigation.BrowseGraph
import io.github.aakocval.petadopt.feature.browse.navigation.browseGraph
import io.github.aakocval.petadopt.feature.explore.navigation.exploreGraph
import io.github.aakocval.petadopt.feature.favorites.navigation.favoritesGraph
import io.github.aakocval.petadopt.feature.petdetail.navigation.navigateToPetDetail
import io.github.aakocval.petadopt.feature.petdetail.navigation.petDetailScreen
import io.github.aakocval.petadopt.feature.profile.navigation.profileGraph

/**
 * Wires every feature graph together. Features only expose `NavGraphBuilder` extensions and take
 * navigation as lambdas; this is the one place that decides what those lambdas do.
 *
 * ```
 * root
 * ├── BrowseGraph ── Browse ─> PetDetail ─> Adoption (Form ─> Review ─> Success)
 * ├── ExploreGraph ─ Explore (map) ─> PetDetail ─> Adoption
 * ├── FavoritesGraph ─ Favorites ─> PetDetail ─> Adoption
 * └── ProfileGraph ── Profile ─(Saved pets)─> switch to Favorites tab
 * ```
 */
@Composable
fun AppNavHost(
    appState: PetAppState,
    onShowSnackbar: suspend (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    val navController = appState.navController
    NavHost(navController = navController, startDestination = BrowseGraph, modifier = modifier) {
        browseGraph(
            onPetClick = navController::navigateToPetDetail,
            onShowSnackbar = onShowSnackbar,
            // Pet Detail and the adoption flow live inside each tab, on that tab's back stack.
            nestedGraphs = { petDetailAndAdoption(navController) },
        )
        exploreGraph(
            onPetClick = navController::navigateToPetDetail,
            nestedGraphs = { petDetailAndAdoption(navController) },
        )
        favoritesGraph(
            onPetClick = navController::navigateToPetDetail,
            nestedGraphs = { petDetailAndAdoption(navController) },
        )
        profileGraph(onOpenFavorites = { appState.navigateToTopLevelDestination(TopLevelDestination.Favorites) })
    }
}

/** Screens any tab can push: a pet's detail, and the adoption flow started from it. */
private fun NavGraphBuilder.petDetailAndAdoption(navController: NavHostController) {
    petDetailScreen(onBack = { navController.popBackStack() }, onApply = navController::navigateToAdoption)
    adoptionGraph(navController)
}
