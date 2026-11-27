package io.github.aakocval.petadopt.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navOptions
import io.github.aakocval.petadopt.feature.adoption.navigation.AdoptionGraph
import io.github.aakocval.petadopt.feature.browse.navigation.navigateToBrowse
import io.github.aakocval.petadopt.feature.explore.navigation.navigateToExplore
import io.github.aakocval.petadopt.feature.favorites.navigation.navigateToFavorites
import io.github.aakocval.petadopt.feature.petdetail.navigation.navigateToPetDetail
import io.github.aakocval.petadopt.feature.profile.navigation.navigateToProfile

/**
 * App-level UI state: which tab is selected and how to move between tabs.
 * Kept out of composables so the navigation rules live in one readable place.
 */
@Stable
class PetAppState(
    val navController: NavHostController,
) {
    /** The tab whose graph contains the current destination (e.g. Pet Detail opened from Favorites -> Favorites). */
    val currentTopLevelDestination: TopLevelDestination?
        @Composable get() {
            val destination by navController.currentBackStackEntryAsState()
            val hierarchy = destination?.destination?.hierarchy ?: return null
            return TopLevelDestination.entries.firstOrNull { tab -> hierarchy.any { it.hasRoute(tab.graph::class) } }
        }

    /** The tab bar steps aside during focused flows (the adoption application) so it can't interrupt them. */
    val shouldShowBottomBar: Boolean
        @Composable get() {
            val entry by navController.currentBackStackEntryAsState()
            return entry?.destination?.hierarchy?.none { it.hasRoute(AdoptionGraph::class) } ?: true
        }

    /**
     * Switches tabs while keeping each tab's back stack:
     * - `popUpTo(start) { saveState = true }` saves the tab we leave instead of destroying it
     * - `restoreState = true` brings back the target tab exactly as it was left
     * - `launchSingleTop` avoids stacking a tab on top of itself
     */
    fun navigateToTopLevelDestination(destination: TopLevelDestination) {
        val options =
            navOptions {
                popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                launchSingleTop = true
                restoreState = true
            }
        when (destination) {
            TopLevelDestination.Browse -> navController.navigateToBrowse(options)
            TopLevelDestination.Explore -> navController.navigateToExplore(options)
            TopLevelDestination.Favorites -> navController.navigateToFavorites(options)
            TopLevelDestination.Profile -> navController.navigateToProfile(options)
        }
    }

    /** Opens a pet from outside the app (a deep link): Browse tab, then its detail on top. */
    fun openPet(petId: String) {
        navigateToTopLevelDestination(TopLevelDestination.Browse)
        navController.navigateToPetDetail(petId)
    }

    /** Re-tapping the selected tab pops it back to its root, like iOS. */
    fun popToRoot(destination: TopLevelDestination) {
        navController.popBackStack(destination.root, inclusive = false)
    }
}

@Composable
fun rememberPetAppState(navController: NavHostController = rememberNavController()): PetAppState =
    remember(navController) { PetAppState(navController) }
