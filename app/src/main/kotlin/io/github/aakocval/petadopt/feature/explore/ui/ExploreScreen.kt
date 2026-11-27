package io.github.aakocval.petadopt.feature.explore.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.onClick
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import io.github.aakocval.petadopt.R
import io.github.aakocval.petadopt.core.designsystem.component.LargeTitle
import io.github.aakocval.petadopt.core.designsystem.icon.PetAdoptIcons
import io.github.aakocval.petadopt.core.designsystem.theme.PetAdoptTheme
import io.github.aakocval.petadopt.core.designsystem.theme.Spacing
import io.github.aakocval.petadopt.core.ui.PetPhoto
import io.github.aakocval.petadopt.core.ui.petSubtitle
import io.github.aakocval.petadopt.domain.model.GeoPoint
import io.github.aakocval.petadopt.domain.model.Species
import io.github.aakocval.petadopt.feature.explore.map.LocalMapProvider
import io.github.aakocval.petadopt.feature.explore.map.MapPin
import kotlinx.collections.immutable.persistentListOf

@Composable
fun ExploreScreen(
    uiState: ExploreUiState,
    onPinSelected: (String?) -> Unit,
    onPetClick: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier.fillMaxSize()) {
        LargeTitle(title = stringResource(R.string.explore_title), subtitle = stringResource(R.string.explore_subtitle))
        Box(
            modifier =
                Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(start = Spacing.screenHorizontal, end = Spacing.screenHorizontal, bottom = Spacing.lg)
                    .clip(MaterialTheme.shapes.large),
        ) {
            LocalMapProvider.current.Map(
                pins = uiState.pins,
                selectedPinId = uiState.selected?.id,
                onPinClick = onPinSelected,
                modifier = Modifier.fillMaxSize(),
            )
            if (uiState.isLoading) CircularProgressIndicator(Modifier.align(Alignment.Center))
            if (!uiState.isLoading && uiState.selected == null) {
                Surface(
                    modifier = Modifier.align(Alignment.TopCenter).padding(Spacing.md),
                    shape = MaterialTheme.shapes.extraLarge,
                    color = MaterialTheme.colorScheme.surface.copy(alpha = 0.9f),
                ) {
                    Text(
                        text = stringResource(R.string.explore_hint),
                        style = MaterialTheme.typography.labelLarge,
                        modifier = Modifier.padding(horizontal = Spacing.md, vertical = Spacing.sm),
                    )
                }
            }
            SelectedPetCard(
                selected = uiState.selected,
                onPetClick = onPetClick,
                modifier = Modifier.align(Alignment.BottomCenter),
            )
        }
    }
}

/** Slides the selected pet's card up from the bottom of the map. */
@Composable
private fun SelectedPetCard(
    selected: ExplorePetCard?,
    onPetClick: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    AnimatedVisibility(
        visible = selected != null,
        enter = slideInVertically { it } + fadeIn(),
        exit = slideOutVertically { it } + fadeOut(),
        modifier = modifier,
    ) {
        // Remember the last pet so the card keeps its content while animating out.
        val pet = remember { mutableStateOf(selected) }.apply { if (selected != null) value = selected }.value
        pet?.let { PetCard(it, onClick = { onPetClick(it.id) }) }
    }
}

/** The "bottom sheet" card for the selected pin. */
@Composable
private fun PetCard(
    pet: ExplorePetCard,
    onClick: () -> Unit,
) {
    val openLabel = stringResource(R.string.explore_open_pet, pet.name)
    Card(
        onClick = onClick,
        modifier =
            Modifier
                .fillMaxWidth()
                .padding(Spacing.md)
                .semantics { onClick(label = openLabel) { onClick().let { true } } },
        shape = MaterialTheme.shapes.large,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
    ) {
        Row(modifier = Modifier.padding(Spacing.md), verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier =
                    Modifier
                        .size(72.dp)
                        .clip(MaterialTheme.shapes.medium),
            ) {
                PetPhoto(photoUrl = pet.photoUrl, species = pet.species)
            }
            Column(modifier = Modifier.weight(1f).padding(horizontal = Spacing.md)) {
                Text(pet.name, style = MaterialTheme.typography.titleMedium)
                Text(
                    text = petSubtitle(pet.breed, pet.ageInMonths),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                Text(
                    text = pet.shelterName,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
            Icon(
                painter = painterResource(PetAdoptIcons.ChevronRight),
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

@PreviewLightDark
@Composable
private fun ExploreScreenPreview() {
    PetAdoptTheme {
        Surface(color = MaterialTheme.colorScheme.background) {
            ExploreScreen(
                uiState =
                    ExploreUiState(
                        isLoading = false,
                        pins =
                            persistentListOf(
                                MapPin("dog-biscuit", GeoPoint(37.7943, -122.3944), "Biscuit"),
                                MapPin("cat-miso", GeoPoint(37.7682, -122.4337), "Miso"),
                                MapPin("dog-otis", GeoPoint(37.7828, -122.4567), "Otis"),
                            ),
                        selected =
                            ExplorePetCard(
                                id = "dog-biscuit",
                                name = "Biscuit",
                                breed = "Beagle",
                                ageInMonths = 26,
                                species = Species.Dog,
                                shelterName = "Harbor Paws Rescue",
                                photoUrl = null,
                            ),
                    ),
                onPinSelected = {},
                onPetClick = {},
            )
        }
    }
}
