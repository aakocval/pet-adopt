package io.github.aakocval.petadopt.core.designsystem.component

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import io.github.aakocval.petadopt.core.designsystem.icon.PetAdoptIcons
import io.github.aakocval.petadopt.core.designsystem.theme.PetAdoptTheme
import io.github.aakocval.petadopt.core.designsystem.theme.Spacing

/**
 * Grid card for one pet: soft pastel background, square photo, name, a secondary line and a
 * favorite toggle.
 *
 * Takes plain values and lambdas only (no domain model), so the design system stays independent
 * of features and every parameter is stable for Compose.
 *
 * @param tileColor background, usually `PetAdoptTheme.colors.tileFor(pet.id)`.
 * @param image photo slot. Defaults to the paw placeholder; Coil plugs in here in M6.
 */
@Composable
fun PetTile(
    name: String,
    subtitle: String,
    tileColor: Color,
    isFavorite: Boolean,
    onClick: () -> Unit,
    onToggleFavorite: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    image: @Composable () -> Unit = { PetPlaceholder() },
) {
    Card(
        onClick = onClick,
        modifier = modifier,
        shape = MaterialTheme.shapes.large,
        colors = CardDefaults.cardColors(containerColor = tileColor),
    ) {
        Box(
            modifier =
                Modifier
                    .padding(Spacing.sm)
                    .fillMaxWidth()
                    .aspectRatio(1f)
                    .clip(MaterialTheme.shapes.medium),
        ) {
            image()
        }
        Row(
            modifier = Modifier.padding(start = Spacing.md, end = Spacing.xxs, bottom = Spacing.xs),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(Spacing.xxs)) {
                Text(
                    text = name,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
            FavoriteButton(isFavorite = isFavorite, onToggle = onToggleFavorite)
        }
    }
}

/**
 * Stand-in for a pet photo while it loads, when it fails, or offline: an illustrated [face]
 * (or a paw if none is given) on a lighter wash of the tile.
 */
@Composable
fun PetPlaceholder(
    modifier: Modifier = Modifier,
    @DrawableRes face: Int? = null,
) {
    Box(
        modifier =
            modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.55f)),
        contentAlignment = Alignment.Center,
    ) {
        if (face != null) {
            // Decorative: the tile's name describes it.
            Image(
                painter = painterResource(face),
                contentDescription = null,
                modifier = Modifier.fillMaxSize(FACE_FRACTION),
            )
        } else {
            Icon(
                painter = painterResource(PetAdoptIcons.Paw),
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                modifier = Modifier.size(48.dp),
            )
        }
    }
}

private const val FACE_FRACTION = 0.6f

@PreviewLightDark
@Composable
private fun PetTilePreview() {
    PetAdoptTheme {
        Surface(color = MaterialTheme.colorScheme.background) {
            Row(modifier = Modifier.padding(Spacing.lg), horizontalArrangement = Arrangement.spacedBy(Spacing.md)) {
                PetTile(
                    name = "Biscuit",
                    subtitle = "Beagle · 2 yrs",
                    tileColor = PetAdoptTheme.colors.tiles[0],
                    isFavorite = true,
                    onClick = {},
                    onToggleFavorite = {},
                    modifier = Modifier.width(170.dp),
                    image = { PetPlaceholder(face = PetAdoptIcons.DogFace) },
                )
                PetTile(
                    name = "Miso",
                    subtitle = "Tabby · 8 mos",
                    tileColor = PetAdoptTheme.colors.tiles[1],
                    isFavorite = false,
                    onClick = {},
                    onToggleFavorite = {},
                    modifier = Modifier.width(170.dp),
                )
            }
        }
    }
}
