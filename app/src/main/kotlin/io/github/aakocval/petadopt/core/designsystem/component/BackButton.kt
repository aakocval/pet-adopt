package io.github.aakocval.petadopt.core.designsystem.component

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.PreviewLightDark
import io.github.aakocval.petadopt.R
import io.github.aakocval.petadopt.core.designsystem.icon.PetAdoptIcons
import io.github.aakocval.petadopt.core.designsystem.theme.PetAdoptTheme
import io.github.aakocval.petadopt.core.designsystem.theme.Spacing

/**
 * Round, translucent back button that floats over imagery. The chevron is auto-mirrored, so it
 * points the right way in right-to-left layouts.
 */
@Composable
fun BackButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    FilledIconButton(
        onClick = onClick,
        modifier = modifier,
        colors =
            IconButtonDefaults.filledIconButtonColors(
                containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.85f),
                contentColor = MaterialTheme.colorScheme.onSurface,
            ),
    ) {
        Icon(
            painter = painterResource(PetAdoptIcons.ChevronLeft),
            contentDescription = stringResource(R.string.ds_back),
        )
    }
}

@PreviewLightDark
@Composable
private fun BackButtonPreview() {
    PetAdoptTheme { BackButton(onClick = {}, modifier = Modifier.padding(Spacing.sm)) }
}
