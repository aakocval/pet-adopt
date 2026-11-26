package io.github.aakocval.petadopt.navigation

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import io.github.aakocval.petadopt.R
import io.github.aakocval.petadopt.core.designsystem.icon.PetAdoptIcons
import io.github.aakocval.petadopt.feature.browse.navigation.BrowseDestination
import io.github.aakocval.petadopt.feature.browse.navigation.BrowseGraph
import io.github.aakocval.petadopt.feature.explore.navigation.ExploreDestination
import io.github.aakocval.petadopt.feature.explore.navigation.ExploreGraph
import io.github.aakocval.petadopt.feature.favorites.navigation.FavoritesDestination
import io.github.aakocval.petadopt.feature.favorites.navigation.FavoritesGraph
import io.github.aakocval.petadopt.feature.profile.navigation.ProfileDestination
import io.github.aakocval.petadopt.feature.profile.navigation.ProfileGraph

/**
 * The bottom tabs. Each one is a nested graph ([graph]) with its own back stack, starting at
 * [root]. This is the only place that lists every feature, which is why it lives in the app's
 * navigation package and not in any feature.
 */
enum class TopLevelDestination(
    @param:StringRes val label: Int,
    @param:DrawableRes val icon: Int,
    val graph: Any,
    val root: Any,
) {
    Browse(R.string.tab_browse, PetAdoptIcons.Paw, BrowseGraph, BrowseDestination),
    Explore(R.string.tab_explore, PetAdoptIcons.MapPin, ExploreGraph, ExploreDestination),
    Favorites(R.string.tab_favorites, PetAdoptIcons.Heart, FavoritesGraph, FavoritesDestination),
    Profile(R.string.tab_profile, PetAdoptIcons.Person, ProfileGraph, ProfileDestination),
}
