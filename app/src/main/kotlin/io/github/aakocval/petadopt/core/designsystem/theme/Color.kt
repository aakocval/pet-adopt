package io.github.aakocval.petadopt.core.designsystem.theme

import androidx.compose.ui.graphics.Color

// Raw palette. Screens never use these directly: they read MaterialTheme.colorScheme
// or PetAdoptTheme.colors, so light/dark switching happens in one place.

// One accent color, used sparingly: primary actions, selected tab, favorite heart.
internal val Tangerine = Color(0xFFF28C38)
internal val TangerineDark = Color(0xFFFFA45C)

// Neutrals (iOS-like system grays).
internal val Ink = Color(0xFF1C1C1E)
internal val Graphite = Color(0xFF8E8E93)
internal val Mist = Color(0xFFE5E5EA)
internal val Paper = Color(0xFFF7F6F3)
internal val White = Color(0xFFFFFFFF)

internal val Night = Color(0xFF000000)
internal val Coal = Color(0xFF1C1C1E)
internal val Smoke = Color(0xFF2C2C2E)
internal val Ash = Color(0xFF98989F)
internal val Snow = Color(0xFFF2F2F7)

internal val Danger = Color(0xFFE5484D)
internal val DangerDark = Color(0xFFFF6369)

// Soft pastel tiles (light) and muted counterparts (dark) that keep text readable.
internal val LightTiles =
    listOf(
        Color(0xFFFFE5D4), // peach
        Color(0xFFD9F2E6), // mint
        Color(0xFFDCEBFA), // sky
        Color(0xFFE8E0F7), // lavender
        Color(0xFFFFF3C9), // butter
        Color(0xFFFBE0E6), // rose
    )

internal val DarkTiles =
    listOf(
        Color(0xFF3D2E24),
        Color(0xFF243A30),
        Color(0xFF243243),
        Color(0xFF332B45),
        Color(0xFF3D3722),
        Color(0xFF42282F),
    )
