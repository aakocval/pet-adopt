package io.github.aakocval.petadopt.core.designsystem.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp

/**
 * iOS-like type scale mapped onto Material 3 slots, using the system font (no bundled fonts,
 * nothing to download, smaller APK).
 *
 * | M3 slot        | iOS equivalent | Use                          |
 * |----------------|----------------|------------------------------|
 * | headlineLarge  | Large Title    | Screen titles                |
 * | headlineMedium | Title 1        | Detail screen name           |
 * | titleLarge     | Title 2        | Section headers              |
 * | titleMedium    | Headline       | Card titles, row titles      |
 * | bodyLarge      | Body           | Paragraphs                   |
 * | bodyMedium     | Subheadline    | Secondary lines              |
 * | bodySmall      | Footnote       | Metadata                     |
 * | labelLarge     | Callout bold   | Buttons                      |
 * | labelMedium    | Caption        | Chips, tab labels            |
 */
internal val PetAdoptTypography =
    Typography(
        headlineLarge =
            TextStyle(
                fontSize = 34.sp,
                lineHeight = 41.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 0.01.em,
            ),
        headlineMedium = TextStyle(fontSize = 28.sp, lineHeight = 34.sp, fontWeight = FontWeight.Bold),
        titleLarge = TextStyle(fontSize = 22.sp, lineHeight = 28.sp, fontWeight = FontWeight.Bold),
        titleMedium = TextStyle(fontSize = 17.sp, lineHeight = 22.sp, fontWeight = FontWeight.SemiBold),
        titleSmall = TextStyle(fontSize = 15.sp, lineHeight = 20.sp, fontWeight = FontWeight.SemiBold),
        bodyLarge = TextStyle(fontSize = 17.sp, lineHeight = 22.sp),
        bodyMedium = TextStyle(fontSize = 15.sp, lineHeight = 20.sp),
        bodySmall = TextStyle(fontSize = 13.sp, lineHeight = 18.sp),
        labelLarge = TextStyle(fontSize = 15.sp, lineHeight = 20.sp, fontWeight = FontWeight.SemiBold),
        labelMedium = TextStyle(fontSize = 12.sp, lineHeight = 16.sp, fontWeight = FontWeight.Medium),
        labelSmall = TextStyle(fontSize = 11.sp, lineHeight = 13.sp, fontWeight = FontWeight.Medium),
    )
