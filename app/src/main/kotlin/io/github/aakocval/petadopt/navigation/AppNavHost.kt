package io.github.aakocval.petadopt.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
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
 * ├── BrowseGraph ── Browse ─> PetDetail
 * ├── ExploreGraph ─ Explore
 * ├── FavoritesGraph ─ Favorites ─> PetDetail
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
            // Pet Detail is registered inside each tab, so it's pushed onto that tab's back stack.
            nestedGraphs = { petDetailScreen(onBack = { navController.popBackStack() }) },
        )
        exploreGraph()
        favoritesGraph(
            onPetClick = navController::navigateToPetDetail,
            nestedGraphs = { petDetailScreen(onBack = { navController.popBackStack() }) },
        )
        profileGraph(onOpenFavorites = { appState.navigateToTopLevelDestination(TopLevelDestination.Favorites) })
    }
}
