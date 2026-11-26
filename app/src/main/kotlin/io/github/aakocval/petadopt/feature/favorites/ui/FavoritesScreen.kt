package io.github.aakocval.petadopt.feature.favorites.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.CircularProgressIndicator
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
import io.github.aakocval.petadopt.core.designsystem.component.LargeTitle
import io.github.aakocval.petadopt.core.designsystem.component.PetTile
import io.github.aakocval.petadopt.core.designsystem.icon.PetAdoptIcons
import io.github.aakocval.petadopt.core.designsystem.theme.PetAdoptTheme
import io.github.aakocval.petadopt.core.designsystem.theme.Spacing
import io.github.aakocval.petadopt.core.ui.PetCardUi
import io.github.aakocval.petadopt.core.ui.petSubtitle
import kotlinx.collections.immutable.persistentListOf

@Composable
fun FavoritesScreen(
    uiState: FavoritesUiState,
    onPetClick: (petId: String) -> Unit,
    onRemoveFavorite: (petId: String) -> Unit,
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
        item(key = "title", span = { GridItemSpan(maxLineSpan) }) {
            LargeTitle(
                title = stringResource(R.string.favorites_title),
                subtitle = stringResource(R.string.favorites_subtitle),
                horizontalPadding = 0.dp,
            )
        }
        when (uiState) {
            FavoritesUiState.Loading -> {
                item(key = "loading", span = { GridItemSpan(maxLineSpan) }) {
                    Box(Modifier.fillMaxWidth().padding(Spacing.xxl), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                }
            }

            FavoritesUiState.Empty -> {
                item(key = "empty", span = { GridItemSpan(maxLineSpan) }) { EmptyFavorites() }
            }

            is FavoritesUiState.Pets -> {
                items(uiState.pets, key = { it.id }, contentType = { "pet" }) { pet ->
                    PetTile(
                        name = pet.name,
                        subtitle = petSubtitle(pet.breed, pet.ageInMonths),
                        tileColor = PetAdoptTheme.colors.tileFor(pet.id),
                        isFavorite = true,
                        onClick = { onPetClick(pet.id) },
                        onToggleFavorite = { onRemoveFavorite(pet.id) },
                        // Animates the grid when a favorite is removed.
                        modifier = Modifier.animateItem(),
                    )
                }
            }
        }
    }
}

@Composable
private fun EmptyFavorites() {
    Column(
        modifier = Modifier.fillMaxWidth().padding(vertical = Spacing.xxl * 2),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(Spacing.sm),
    ) {
        Icon(
            painter = painterResource(PetAdoptIcons.HeartOutline),
            contentDescription = null,
            tint = PetAdoptTheme.colors.favorite,
            modifier = Modifier.size(48.dp),
        )
        Text(stringResource(R.string.favorites_empty_title), style = MaterialTheme.typography.titleLarge)
        Text(
            text = stringResource(R.string.favorites_empty_body),
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
        )
    }
}

@PreviewLightDark
@Composable
private fun FavoritesScreenPreview() {
    PetAdoptTheme {
        Surface(color = MaterialTheme.colorScheme.background) {
            FavoritesScreen(
                uiState =
                    FavoritesUiState.Pets(
                        persistentListOf(
                            PetCardUi("dog-biscuit", "Biscuit", "Beagle", ageInMonths = 26, isFavorite = true),
                            PetCardUi("cat-luna", "Luna", "Siamese", ageInMonths = 14, isFavorite = true),
                        ),
                    ),
                onPetClick = {},
                onRemoveFavorite = {},
            )
        }
    }
}

@PreviewLightDark
@Composable
private fun FavoritesEmptyPreview() {
    PetAdoptTheme {
        Surface(color = MaterialTheme.colorScheme.background) {
            FavoritesScreen(uiState = FavoritesUiState.Empty, onPetClick = {}, onRemoveFavorite = {})
        }
    }
}
