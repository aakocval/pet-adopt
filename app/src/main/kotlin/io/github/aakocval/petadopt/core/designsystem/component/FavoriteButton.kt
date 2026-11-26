package io.github.aakocval.petadopt.core.designsystem.component

import androidx.compose.material3.Icon
import androidx.compose.material3.IconToggleButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import io.github.aakocval.petadopt.R
import io.github.aakocval.petadopt.core.designsystem.icon.PetAdoptIcons
import io.github.aakocval.petadopt.core.designsystem.theme.PetAdoptTheme

/**
 * Heart toggle. A real toggle (not a clickable icon), so TalkBack announces "on/off" and the
 * touch target is the 48dp minimum.
 */
@Composable
fun FavoriteButton(
    isFavorite: Boolean,
    onToggle: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
) {
    IconToggleButton(checked = isFavorite, onCheckedChange = onToggle, modifier = modifier) {
        Icon(
            painter = painterResource(if (isFavorite) PetAdoptIcons.Heart else PetAdoptIcons.HeartOutline),
            contentDescription =
                stringResource(
                    if (isFavorite) R.string.ds_favorite_remove else R.string.ds_favorite_add,
                ),
            tint = if (isFavorite) PetAdoptTheme.colors.favorite else MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}
