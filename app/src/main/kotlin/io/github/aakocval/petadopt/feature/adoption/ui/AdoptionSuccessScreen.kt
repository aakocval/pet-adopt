package io.github.aakocval.petadopt.feature.adoption.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import io.github.aakocval.petadopt.R
import io.github.aakocval.petadopt.core.designsystem.icon.PetAdoptIcons
import io.github.aakocval.petadopt.core.designsystem.theme.PetAdoptTheme
import io.github.aakocval.petadopt.core.designsystem.theme.Spacing

@Composable
internal fun AdoptionSuccessScreen(
    uiState: AdoptionUiState,
    onDone: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier =
            modifier
                .fillMaxSize()
                .padding(Spacing.xl),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Box(
            modifier =
                Modifier
                    .size(120.dp)
                    .background(PetAdoptTheme.colors.tileFor("adoption-success"), CircleShape),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                painter = painterResource(PetAdoptIcons.Heart),
                contentDescription = null,
                tint = PetAdoptTheme.colors.favorite,
                modifier = Modifier.size(56.dp),
            )
        }
        Spacer(Modifier.height(Spacing.xl))
        Text(stringResource(R.string.adoption_success_title), style = MaterialTheme.typography.headlineMedium)
        Spacer(Modifier.height(Spacing.sm))
        uiState.petName?.let {
            Text(
                text = stringResource(R.string.adoption_success_body, it),
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
            )
        }
        uiState.reference?.let {
            Spacer(Modifier.height(Spacing.lg))
            Surface(shape = MaterialTheme.shapes.extraLarge, color = MaterialTheme.colorScheme.surfaceVariant) {
                Text(
                    text = stringResource(R.string.adoption_success_reference, it),
                    style = MaterialTheme.typography.labelLarge,
                    modifier = Modifier.padding(horizontal = Spacing.lg, vertical = Spacing.sm),
                )
            }
        }
        Spacer(Modifier.height(Spacing.xxl))
        Button(
            onClick = onDone,
            modifier = Modifier.fillMaxWidth().height(52.dp),
            shape = MaterialTheme.shapes.medium,
        ) { Text(stringResource(R.string.adoption_done)) }
    }
}

@PreviewLightDark
@Composable
private fun AdoptionSuccessScreenPreview() {
    PetAdoptTheme {
        Surface(color = MaterialTheme.colorScheme.background) {
            AdoptionSuccessScreen(uiState = AdoptionUiState(petName = "Biscuit", reference = "PA-7F3K2Q"), onDone = {})
        }
    }
}
