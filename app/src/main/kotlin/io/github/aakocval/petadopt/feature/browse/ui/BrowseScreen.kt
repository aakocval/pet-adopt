package io.github.aakocval.petadopt.feature.browse.ui

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyGridScope
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import io.github.aakocval.petadopt.R
import io.github.aakocval.petadopt.core.designsystem.component.LargeTitle
import io.github.aakocval.petadopt.core.designsystem.component.PetTile
import io.github.aakocval.petadopt.core.designsystem.theme.PetAdoptTheme
import io.github.aakocval.petadopt.core.designsystem.theme.Spacing
import kotlinx.collections.immutable.persistentListOf

/**
 * Stateless Browse screen: state in, events out. No ViewModel, so it previews and UI-tests with
 * plain values.
 */
@Composable
fun BrowseScreen(
    uiState: BrowseUiState,
    onFilterSelected: (SpeciesFilter) -> Unit,
    onPetClick: (petId: String) -> Unit,
    onToggleFavorite: (petId: String, isFavorite: Boolean) -> Unit,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier,
) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        modifier = modifier.fillMaxSize(),
        contentPadding =
            PaddingValues(
                start = Spacing.screenHorizontal,
                end = Spacing.screenHorizontal,
                bottom = Spacing.xl,
            ),
        horizontalArrangement = Arrangement.spacedBy(Spacing.md),
        verticalArrangement = Arrangement.spacedBy(Spacing.md),
    ) {
        fullWidthItem(key = "title") {
            LargeTitle(
                title = stringResource(R.string.browse_title),
                subtitle = stringResource(R.string.browse_subtitle),
                horizontalPadding = 0.dp,
            )
        }
        fullWidthItem(key = "filters") {
            FilterRow(selected = uiState.filter, onSelected = onFilterSelected)
        }
        when (val content = uiState.content) {
            BrowseContent.Loading -> {
                fullWidthItem(key = "loading") {
                    Box(Modifier.fillMaxWidth().padding(Spacing.xxl), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                }
            }

            BrowseContent.Error -> {
                fullWidthItem(key = "error") { ErrorMessage(onRetry = onRetry) }
            }

            is BrowseContent.Pets -> {
                if (content.pets.isEmpty()) {
                    fullWidthItem(key = "empty") { CenteredMessage(R.string.browse_empty) }
                } else {
                    // Stable keys: items keep their state and animate correctly when the list changes.
                    items(content.pets, key = { it.id }, contentType = { "pet" }) { pet ->
                        PetTile(
                            name = pet.name,
                            subtitle = stringResource(R.string.pet_subtitle, pet.breed, ageLabel(pet.ageInMonths)),
                            tileColor = PetAdoptTheme.colors.tileFor(pet.id),
                            isFavorite = pet.isFavorite,
                            onClick = { onPetClick(pet.id) },
                            onToggleFavorite = { onToggleFavorite(pet.id, it) },
                        )
                    }
                }
            }
        }
    }
}

private fun LazyGridScope.fullWidthItem(
    key: String,
    content: @Composable () -> Unit,
) {
    item(key = key, span = { GridItemSpan(maxLineSpan) }, contentType = key) { content() }
}

@Composable
private fun FilterRow(
    selected: SpeciesFilter,
    onSelected: (SpeciesFilter) -> Unit,
) {
    Row(horizontalArrangement = Arrangement.spacedBy(Spacing.sm)) {
        SpeciesFilter.entries.forEach { filter ->
            FilterChip(
                selected = filter == selected,
                onClick = { onSelected(filter) },
                label = { Text(stringResource(filter.labelRes)) },
                shape = MaterialTheme.shapes.extraLarge,
                colors =
                    FilterChipDefaults.filterChipColors(
                        selectedContainerColor = MaterialTheme.colorScheme.onSurface,
                        selectedLabelColor = MaterialTheme.colorScheme.surface,
                    ),
            )
        }
    }
}

@get:StringRes
private val SpeciesFilter.labelRes: Int
    get() =
        when (this) {
            SpeciesFilter.All -> R.string.browse_filter_all
            SpeciesFilter.Dogs -> R.string.browse_filter_dogs
            SpeciesFilter.Cats -> R.string.browse_filter_cats
        }

/** "8 mos" under a year, "2 yrs" after. Pluralized per locale via resources. */
@Composable
private fun ageLabel(ageInMonths: Int): String =
    if (ageInMonths < MONTHS_PER_YEAR) {
        pluralStringResource(R.plurals.pet_age_months, ageInMonths, ageInMonths)
    } else {
        val years = ageInMonths / MONTHS_PER_YEAR
        pluralStringResource(R.plurals.pet_age_years, years, years)
    }

private const val MONTHS_PER_YEAR = 12

@Composable
private fun CenteredMessage(
    @StringRes text: Int,
) {
    Text(
        text = stringResource(text),
        style = MaterialTheme.typography.bodyLarge,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        textAlign = TextAlign.Center,
        modifier = Modifier.fillMaxWidth().padding(vertical = Spacing.xxl),
    )
}

@Composable
private fun ErrorMessage(onRetry: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxWidth().padding(vertical = Spacing.xl),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(Spacing.md),
    ) {
        CenteredMessage(R.string.browse_error)
        Button(onClick = onRetry) { Text(stringResource(R.string.browse_retry)) }
    }
}

private val PreviewPets =
    persistentListOf(
        PetCardUi("dog-biscuit", "Biscuit", "Beagle", ageInMonths = 26, isFavorite = true),
        PetCardUi("cat-miso", "Miso", "Tabby", ageInMonths = 8, isFavorite = false),
        PetCardUi("dog-pepper", "Pepper", "Corgi", ageInMonths = 50, isFavorite = false),
        PetCardUi("cat-luna", "Luna", "Siamese", ageInMonths = 14, isFavorite = false),
    )

@PreviewLightDark
@Composable
private fun BrowseScreenPetsPreview() {
    PetAdoptTheme {
        Surface(color = MaterialTheme.colorScheme.background) {
            BrowseScreen(
                uiState = BrowseUiState(content = BrowseContent.Pets(PreviewPets)),
                onFilterSelected = {},
                onPetClick = {},
                onToggleFavorite = { _, _ -> },
                onRetry = {},
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun BrowseScreenErrorPreview() {
    PetAdoptTheme {
        BrowseScreen(
            uiState = BrowseUiState(filter = SpeciesFilter.Cats, content = BrowseContent.Error),
            onFilterSelected = {},
            onPetClick = {},
            onToggleFavorite = { _, _ -> },
            onRetry = {},
        )
    }
}
