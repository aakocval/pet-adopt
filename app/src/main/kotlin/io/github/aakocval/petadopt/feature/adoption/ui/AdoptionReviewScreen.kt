package io.github.aakocval.petadopt.feature.adoption.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import io.github.aakocval.petadopt.R
import io.github.aakocval.petadopt.core.designsystem.theme.PetAdoptTheme
import io.github.aakocval.petadopt.core.designsystem.theme.Spacing
import io.github.aakocval.petadopt.domain.model.HomeType
import io.github.aakocval.petadopt.domain.validation.ApplicationDraft

/** Read-only summary of the shared draft, then submit. */
@Composable
internal fun AdoptionReviewScreen(
    uiState: AdoptionUiState,
    onEdit: () -> Unit,
    onSubmit: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val draft = uiState.draft
    Column(modifier = modifier.fillMaxSize()) {
        AdoptionTopBar(title = stringResource(R.string.adoption_review_title), step = 2, onBack = onEdit)
        Column(
            modifier =
                Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
                    .padding(Spacing.screenHorizontal),
            verticalArrangement = Arrangement.spacedBy(Spacing.lg),
        ) {
            Text(adoptionTitle(uiState.petName), style = MaterialTheme.typography.headlineMedium)
            Surface(shape = MaterialTheme.shapes.medium, color = MaterialTheme.colorScheme.surface) {
                Column {
                    SummaryRow(stringResource(R.string.adoption_full_name), draft.fullName.trim())
                    SummaryDivider()
                    SummaryRow(stringResource(R.string.adoption_email), draft.email)
                    SummaryDivider()
                    SummaryRow(stringResource(R.string.adoption_phone), draft.phone.trim())
                    SummaryDivider()
                    SummaryRow(
                        stringResource(R.string.adoption_home_type),
                        draft.homeType?.let { homeTypeLabel(it) }.orEmpty(),
                    )
                    SummaryDivider()
                    SummaryRow(
                        stringResource(R.string.adoption_other_pets),
                        stringResource(if (draft.hasOtherPets) R.string.adoption_yes else R.string.adoption_no),
                    )
                }
            }
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = MaterialTheme.shapes.medium,
                color = MaterialTheme.colorScheme.surface,
            ) {
                Column(
                    modifier = Modifier.padding(Spacing.lg),
                    verticalArrangement = Arrangement.spacedBy(Spacing.xs),
                ) {
                    Text(
                        stringResource(R.string.adoption_message),
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                    Text(draft.message.trim(), style = MaterialTheme.typography.bodyLarge)
                }
            }
            if (uiState.submitFailed) {
                Text(
                    text = stringResource(R.string.adoption_submit_failed),
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.semantics { liveRegion = LiveRegionMode.Polite },
                )
            }
        }
        Row(
            modifier = Modifier.padding(Spacing.screenHorizontal),
            horizontalArrangement = Arrangement.spacedBy(Spacing.md),
        ) {
            OutlinedButton(
                onClick = onEdit,
                enabled = !uiState.isSubmitting,
                modifier = Modifier.weight(1f).height(52.dp),
                shape = MaterialTheme.shapes.medium,
            ) { Text(stringResource(R.string.adoption_edit)) }
            Button(
                onClick = onSubmit,
                enabled = !uiState.isSubmitting,
                modifier = Modifier.weight(2f).height(52.dp),
                shape = MaterialTheme.shapes.medium,
            ) {
                if (uiState.isSubmitting) {
                    CircularProgressIndicator(modifier = Modifier.size(20.dp), strokeWidth = 2.dp)
                } else {
                    Text(stringResource(R.string.adoption_submit))
                }
            }
        }
    }
}

@Composable
private fun SummaryRow(
    label: String,
    value: String,
) {
    Row(modifier = Modifier.fillMaxWidth().padding(Spacing.lg)) {
        Text(label, color = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.weight(1f))
        Text(value, style = MaterialTheme.typography.bodyLarge)
    }
}

@Composable
private fun SummaryDivider() {
    HorizontalDivider(modifier = Modifier.padding(start = Spacing.lg), color = MaterialTheme.colorScheme.outline)
}

@PreviewLightDark
@Composable
private fun AdoptionReviewScreenPreview() {
    PetAdoptTheme {
        Surface(color = MaterialTheme.colorScheme.background) {
            AdoptionReviewScreen(
                uiState =
                    AdoptionUiState(
                        petName = "Biscuit",
                        draft =
                            ApplicationDraft(
                                fullName = "Alex Morgan",
                                email = "alex@example.com",
                                phone = "+1 555 010 2030",
                                homeType = HomeType.House,
                                hasOtherPets = true,
                                message = "We have a big garden and love long walks.",
                                agreesToHomeVisit = true,
                            ),
                    ),
                onEdit = {},
                onSubmit = {},
            )
        }
    }
}
