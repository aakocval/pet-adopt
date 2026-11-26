package io.github.aakocval.petadopt.feature.profile.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import io.github.aakocval.petadopt.R
import io.github.aakocval.petadopt.core.designsystem.component.LargeTitle
import io.github.aakocval.petadopt.core.designsystem.component.ListRow
import io.github.aakocval.petadopt.core.designsystem.icon.PetAdoptIcons
import io.github.aakocval.petadopt.core.designsystem.theme.PetAdoptTheme
import io.github.aakocval.petadopt.core.designsystem.theme.Spacing

@Composable
fun ProfileScreen(
    uiState: ProfileUiState,
    onOpenFavorites: () -> Unit,
    onSignOut: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier =
            modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(Spacing.lg),
    ) {
        LargeTitle(title = stringResource(R.string.profile_title))
        Card {
            Row(
                modifier = Modifier.padding(Spacing.lg),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(Spacing.md),
            ) {
                Box(
                    modifier =
                        Modifier
                            .size(56.dp)
                            .background(PetAdoptTheme.colors.tileFor("profile-avatar"), CircleShape),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(painterResource(PetAdoptIcons.Person), contentDescription = null)
                }
                Column {
                    Text(
                        uiState.userName ?: stringResource(R.string.profile_guest_name),
                        style = MaterialTheme.typography.titleMedium,
                    )
                    Text(
                        text = uiState.userEmail ?: stringResource(R.string.profile_guest_body),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }
        }
        Card {
            ListRow(
                title = stringResource(R.string.profile_saved_pets),
                subtitle =
                    if (uiState.favoriteCount == 0) {
                        stringResource(R.string.profile_saved_pets_none)
                    } else {
                        pluralStringResource(
                            R.plurals.profile_saved_pets_count,
                            uiState.favoriteCount,
                            uiState.favoriteCount,
                        )
                    },
                icon = PetAdoptIcons.Heart,
                iconBackground = PetAdoptTheme.colors.tileFor("profile-saved"),
                onClick = onOpenFavorites,
            )
            HorizontalDivider(modifier = Modifier.padding(start = 60.dp), color = MaterialTheme.colorScheme.outline)
            ListRow(
                title = stringResource(R.string.profile_version),
                subtitle = uiState.appVersion,
                icon = PetAdoptIcons.Paw,
                iconBackground = PetAdoptTheme.colors.tileFor("profile-version"),
            )
        }
        Card {
            Text(
                text = stringResource(R.string.profile_sign_out),
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.error,
                textAlign = TextAlign.Center,
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .clickable(role = Role.Button, onClick = onSignOut)
                        .padding(Spacing.lg),
            )
        }
    }
}

/** Grouped, rounded section like iOS Settings. */
@Composable
private fun Card(content: @Composable () -> Unit) {
    Surface(
        modifier =
            Modifier
                .fillMaxWidth()
                .padding(horizontal = Spacing.screenHorizontal),
        shape = MaterialTheme.shapes.medium,
        color = MaterialTheme.colorScheme.surface,
    ) {
        Column { content() }
    }
}

@PreviewLightDark
@Composable
private fun ProfileScreenPreview() {
    PetAdoptTheme {
        Surface(color = MaterialTheme.colorScheme.background) {
            ProfileScreen(
                uiState =
                    ProfileUiState(
                        userName = "Alex",
                        userEmail = "alex@example.com",
                        favoriteCount = 3,
                        appVersion = "0.5.0",
                    ),
                onOpenFavorites = {},
                onSignOut = {},
            )
        }
    }
}
