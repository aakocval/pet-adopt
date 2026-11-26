package io.github.aakocval.petadopt.feature.explore.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import io.github.aakocval.petadopt.R
import io.github.aakocval.petadopt.core.designsystem.component.LargeTitle
import io.github.aakocval.petadopt.core.designsystem.icon.PetAdoptIcons
import io.github.aakocval.petadopt.core.designsystem.theme.PetAdoptTheme
import io.github.aakocval.petadopt.core.designsystem.theme.Spacing

/**
 * Placeholder so the tab structure is complete. Replaced by the Canvas mock map behind a
 * MapProvider interface in M6.
 */
@Composable
fun ExploreScreen(modifier: Modifier = Modifier) {
    Column(modifier = modifier.fillMaxSize()) {
        LargeTitle(title = stringResource(R.string.explore_title), subtitle = stringResource(R.string.explore_subtitle))
        Surface(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(Spacing.screenHorizontal),
            shape = MaterialTheme.shapes.large,
            color = PetAdoptTheme.colors.tileFor("explore"),
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(Spacing.md, Alignment.CenterVertically),
            ) {
                Icon(
                    painter = painterResource(PetAdoptIcons.MapPin),
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(48.dp),
                )
                Text(
                    text = stringResource(R.string.explore_placeholder),
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
    }
}

@PreviewLightDark
@Composable
private fun ExploreScreenPreview() {
    PetAdoptTheme { Surface(color = MaterialTheme.colorScheme.background) { ExploreScreen() } }
}
