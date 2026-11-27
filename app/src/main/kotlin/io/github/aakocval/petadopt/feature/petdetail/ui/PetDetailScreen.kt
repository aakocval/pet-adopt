package io.github.aakocval.petadopt.feature.petdetail.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import io.github.aakocval.petadopt.R
import io.github.aakocval.petadopt.core.designsystem.component.BackButton
import io.github.aakocval.petadopt.core.designsystem.component.FavoriteButton
import io.github.aakocval.petadopt.core.designsystem.component.ListRow
import io.github.aakocval.petadopt.core.designsystem.icon.PetAdoptIcons
import io.github.aakocval.petadopt.core.designsystem.theme.PetAdoptTheme
import io.github.aakocval.petadopt.core.designsystem.theme.Spacing
import io.github.aakocval.petadopt.core.ui.PetPhoto
import io.github.aakocval.petadopt.core.ui.petAgeLabel
import io.github.aakocval.petadopt.domain.model.Sex
import io.github.aakocval.petadopt.domain.model.Species

/** Stateless Pet Detail screen: big pastel photo header, quick facts, bio and shelter. */
@Composable
fun PetDetailScreen(
    uiState: PetDetailUiState,
    onBack: () -> Unit,
    onToggleFavorite: (Boolean) -> Unit,
    onApply: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(modifier = modifier.fillMaxSize()) {
        when (uiState) {
            PetDetailUiState.Loading -> {
                CircularProgressIndicator(Modifier.align(Alignment.Center))
            }

            PetDetailUiState.NotFound -> {
                NotFound(Modifier.align(Alignment.Center))
            }

            is PetDetailUiState.Success -> {
                PetDetailContent(
                    pet = uiState.pet,
                    onToggleFavorite = onToggleFavorite,
                    onApply = onApply,
                )
            }
        }
        BackButton(onClick = onBack, modifier = Modifier.padding(Spacing.md))
    }
}

@Composable
private fun PetDetailContent(
    pet: PetDetailUi,
    onToggleFavorite: (Boolean) -> Unit,
    onApply: () -> Unit,
) {
    val tileColor = PetAdoptTheme.colors.tileFor(pet.id)
    Column(
        modifier =
            Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
    ) {
        Box(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .background(tileColor)
                    .padding(start = Spacing.lg, end = Spacing.lg, top = Spacing.xxl * 2, bottom = Spacing.lg),
            contentAlignment = Alignment.Center,
        ) {
            // Height is capped so the photo doesn't fill a landscape or tablet screen;
            // height-first sizing keeps the aspect ratio inside that cap.
            Box(
                modifier =
                    Modifier
                        .heightIn(max = MaxPhotoHeight)
                        .aspectRatio(PHOTO_ASPECT_RATIO, matchHeightConstraintsFirst = true)
                        .clip(MaterialTheme.shapes.large),
            ) { PetPhoto(photoUrl = pet.photoUrl, species = pet.species) }
        }
        Column(
            modifier = Modifier.padding(Spacing.screenHorizontal),
            verticalArrangement = Arrangement.spacedBy(Spacing.lg),
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = pet.name,
                    style = MaterialTheme.typography.headlineMedium,
                    modifier =
                        Modifier
                            .weight(1f)
                            .semantics { heading() },
                )
                FavoriteButton(isFavorite = pet.isFavorite, onToggle = onToggleFavorite)
            }
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(Spacing.sm),
                verticalArrangement = Arrangement.spacedBy(Spacing.sm),
            ) {
                Fact(pet.breed)
                Fact(petAgeLabel(pet.ageInMonths))
                Fact(stringResource(if (pet.sex == Sex.Male) R.string.pet_sex_male else R.string.pet_sex_female))
            }
            Text(
                text = stringResource(R.string.pet_detail_about, pet.name),
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.semantics { heading() },
            )
            Text(text = pet.bio, style = MaterialTheme.typography.bodyLarge)
            Surface(shape = MaterialTheme.shapes.medium, color = MaterialTheme.colorScheme.surface) {
                ListRow(
                    title = pet.shelterName,
                    subtitle = stringResource(R.string.pet_detail_shelter),
                    icon = PetAdoptIcons.MapPin,
                    iconBackground = tileColor,
                )
            }
            Button(
                onClick = onApply,
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .padding(vertical = Spacing.sm)
                        .height(52.dp),
                shape = MaterialTheme.shapes.medium,
            ) { Text(stringResource(R.string.pet_detail_apply, pet.name)) }
        }
    }
}

/** A soft pill for one quick fact (breed, age, sex). */
@Composable
private fun Fact(text: String) {
    Surface(shape = MaterialTheme.shapes.extraLarge, color = MaterialTheme.colorScheme.surfaceVariant) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelLarge,
            modifier = Modifier.padding(horizontal = Spacing.md, vertical = Spacing.sm),
        )
    }
}

@Composable
private fun NotFound(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier.padding(Spacing.xl),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(Spacing.sm),
    ) {
        Text(stringResource(R.string.pet_detail_not_found_title), style = MaterialTheme.typography.titleLarge)
        Text(
            text = stringResource(R.string.pet_detail_not_found_body),
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
        )
    }
}

private const val PHOTO_ASPECT_RATIO = 1.25f
private val MaxPhotoHeight = 320.dp

@PreviewLightDark
@Composable
private fun PetDetailScreenPreview() {
    PetAdoptTheme {
        Surface(color = MaterialTheme.colorScheme.background) {
            PetDetailScreen(
                uiState =
                    PetDetailUiState.Success(
                        PetDetailUi(
                            id = "dog-biscuit",
                            name = "Biscuit",
                            species = Species.Dog,
                            breed = "Beagle",
                            ageInMonths = 26,
                            sex = Sex.Male,
                            bio = "Nose-first explorer who loves long sniffy walks and a warm lap afterwards.",
                            shelterName = "Harbor Paws Rescue",
                            isFavorite = true,
                        ),
                    ),
                onBack = {},
                onToggleFavorite = {},
                onApply = {},
            )
        }
    }
}

@PreviewLightDark
@Composable
private fun PetDetailNotFoundPreview() {
    PetAdoptTheme {
        Surface(color = MaterialTheme.colorScheme.background) {
            PetDetailScreen(uiState = PetDetailUiState.NotFound, onBack = {}, onToggleFavorite = {}, onApply = {})
        }
    }
}
