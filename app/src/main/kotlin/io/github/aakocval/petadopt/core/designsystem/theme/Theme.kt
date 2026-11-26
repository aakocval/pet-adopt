package io.github.aakocval.petadopt.core.designsystem.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

private val LightColors =
    lightColorScheme(
        primary = Tangerine,
        onPrimary = White,
        background = Paper,
        onBackground = Ink,
        surface = White,
        onSurface = Ink,
        surfaceVariant = Snow,
        onSurfaceVariant = Graphite,
        surfaceContainer = White,
        outline = Mist,
        outlineVariant = Mist,
        error = Danger,
    )

private val DarkColors =
    darkColorScheme(
        primary = TangerineDark,
        onPrimary = Night,
        background = Night,
        onBackground = Snow,
        surface = Coal,
        onSurface = Snow,
        surfaceVariant = Smoke,
        onSurfaceVariant = Ash,
        surfaceContainer = Coal,
        outline = Smoke,
        outlineVariant = Smoke,
        error = DangerDark,
    )

/** Brand colors Material 3 has no slot for. */
@Immutable
data class PetAdoptColors(
    val tiles: List<Color>,
    val favorite: Color,
) {
    /** The pastel tile for a pet (or any stable key). */
    fun tileFor(key: String): Color = tiles[tileIndexFor(key, tiles.size)]
}

private val LightExtended = PetAdoptColors(tiles = LightTiles, favorite = Tangerine)
private val DarkExtended = PetAdoptColors(tiles = DarkTiles, favorite = TangerineDark)

// Static: the value only changes with the theme, so a change recomposes everything anyway.
private val LocalPetAdoptColors = staticCompositionLocalOf { LightExtended }

/**
 * App theme. Dynamic color (Material You) is intentionally off: the pastel tiles and the single
 * accent are the brand, and wallpaper-derived colors would fight them.
 */
@Composable
fun PetAdoptTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit,
) {
    CompositionLocalProvider(LocalPetAdoptColors provides if (darkTheme) DarkExtended else LightExtended) {
        MaterialTheme(
            colorScheme = if (darkTheme) DarkColors else LightColors,
            typography = PetAdoptTypography,
            shapes = PetAdoptShapes,
            content = content,
        )
    }
}

/** Accessors for theme values beyond [MaterialTheme], e.g. `PetAdoptTheme.colors.tileFor(id)`. */
object PetAdoptTheme {
    val colors: PetAdoptColors
        @Composable
        @ReadOnlyComposable
        get() = LocalPetAdoptColors.current
}
