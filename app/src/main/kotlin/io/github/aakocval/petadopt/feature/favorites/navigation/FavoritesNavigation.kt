package io.github.aakocval.petadopt.feature.favorites.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import io.github.aakocval.petadopt.feature.favorites.ui.FavoritesRoute
import kotlinx.serialization.Serializable

/** The Favorites tab's nested graph (its own back stack). */
@Serializable
data object FavoritesGraph

@Serializable
data object FavoritesDestination

fun NavController.navigateToFavorites(navOptions: NavOptions? = null) = navigate(FavoritesGraph, navOptions)

fun NavGraphBuilder.favoritesGraph(
    onPetClick: (petId: String) -> Unit,
    nestedGraphs: NavGraphBuilder.() -> Unit = {},
) {
    navigation<FavoritesGraph>(startDestination = FavoritesDestination) {
        composable<FavoritesDestination> { FavoritesRoute(onPetClick = onPetClick) }
        nestedGraphs()
    }
}
