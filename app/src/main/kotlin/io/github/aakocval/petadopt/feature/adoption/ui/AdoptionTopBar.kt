package io.github.aakocval.petadopt.feature.adoption.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import io.github.aakocval.petadopt.R
import io.github.aakocval.petadopt.core.designsystem.component.BackButton
import io.github.aakocval.petadopt.core.designsystem.theme.Spacing

/** Back button, "Step n of 2" and the screen title. */
@Composable
internal fun AdoptionTopBar(
    title: String,
    step: Int,
    onBack: () -> Unit,
) {
    Row(
        modifier =
            Modifier
                .fillMaxWidth()
                .padding(horizontal = Spacing.md, vertical = Spacing.sm),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        BackButton(onClick = onBack)
        Column(modifier = Modifier.padding(start = Spacing.sm)) {
            Text(
                text = stringResource(R.string.adoption_step, step, STEP_COUNT),
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Text(text = title, style = MaterialTheme.typography.titleLarge, modifier = Modifier.semantics { heading() })
        }
    }
}

private const val STEP_COUNT = 2
