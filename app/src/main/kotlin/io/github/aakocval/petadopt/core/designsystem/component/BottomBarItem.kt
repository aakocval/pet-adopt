package io.github.aakocval.petadopt.core.designsystem.component

import androidx.annotation.DrawableRes
import androidx.compose.runtime.Immutable

/** One tab. The design system knows nothing about routes; navigation maps its destinations to these. */
@Immutable
data class BottomBarItem(
    val label: String,
    @param:DrawableRes val icon: Int,
)
