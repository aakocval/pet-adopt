package io.github.aakocval.petadopt.feature.explore.map

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Modifier
import io.github.aakocval.petadopt.domain.model.GeoPoint
import kotlinx.collections.immutable.ImmutableList

/** Something to show on a map. Library-agnostic: any MapProvider can draw it. */
@Immutable
data class MapPin(
    val id: String,
    val location: GeoPoint,
    /** Spoken by TalkBack ("Show Biscuit"), since a map isn't readable otherwise. */
    val label: String,
)

/**
 * Draws a map with pins. The Explore screen only talks to this interface, so the map
 * implementation can change without touching it:
 *
 * - [CanvasMapProvider] (default): a drawn mock map, no API key, free to clone and run
 * - Google Maps Compose or MapLibre: implement this interface and change the binding in MapModule
 *   (see docs/MAP.md)
 */
interface MapProvider {
    @Composable
    fun Map(
        pins: ImmutableList<MapPin>,
        selectedPinId: String?,
        onPinClick: (pinId: String?) -> Unit,
        modifier: Modifier,
    )
}

/** Provided once by the activity from the Hilt binding; defaults to the Canvas map for previews. */
val LocalMapProvider = staticCompositionLocalOf<MapProvider> { CanvasMapProvider() }
