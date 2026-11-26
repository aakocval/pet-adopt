package io.github.aakocval.petadopt.core.designsystem

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.PreviewLightDark
import io.github.aakocval.petadopt.core.designsystem.component.BottomBarItem
import io.github.aakocval.petadopt.core.designsystem.component.LargeTitle
import io.github.aakocval.petadopt.core.designsystem.component.ListRow
import io.github.aakocval.petadopt.core.designsystem.component.PetAdoptBottomBar
import io.github.aakocval.petadopt.core.designsystem.component.PetTile
import io.github.aakocval.petadopt.core.designsystem.icon.PetAdoptIcons
import io.github.aakocval.petadopt.core.designsystem.theme.PetAdoptTheme
import io.github.aakocval.petadopt.core.designsystem.theme.Spacing

/**
 * Temporary launch screen that shows every design system component together.
 * Replaced by the real navigation host in M3.
 */
@Composable
fun DesignSystemGallery(modifier: Modifier = Modifier) {
    val tabs =
        remember {
            listOf(
                BottomBarItem("Browse", PetAdoptIcons.Paw),
                BottomBarItem("Explore", PetAdoptIcons.MapPin),
                BottomBarItem("Favorites", PetAdoptIcons.Heart),
                BottomBarItem("Profile", PetAdoptIcons.Person),
            )
        }
    var selectedTab by rememberSaveable { mutableIntStateOf(0) }
    var favorites by remember { mutableStateOf(setOf("biscuit")) }

    Scaffold(
        modifier = modifier,
        containerColor = MaterialTheme.colorScheme.background,
        bottomBar = { PetAdoptBottomBar(items = tabs, selectedIndex = selectedTab, onSelect = { selectedTab = it }) },
    ) { padding ->
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            modifier =
                Modifier
                    .fillMaxSize()
                    .padding(padding),
            contentPadding = PaddingValues(bottom = Spacing.xl),
            horizontalArrangement = Arrangement.spacedBy(Spacing.md),
            verticalArrangement = Arrangement.spacedBy(Spacing.md),
        ) {
            item(span = { GridItemSpan(maxLineSpan) }, key = "title") {
                LargeTitle(title = "Browse", subtitle = "Design system")
            }
            itemsIndexed(SamplePets, key = { _, pet -> pet.id }) { index, pet ->
                PetTile(
                    name = pet.name,
                    subtitle = pet.subtitle,
                    tileColor = PetAdoptTheme.colors.tileFor(pet.id),
                    isFavorite = pet.id in favorites,
                    onClick = {},
                    onToggleFavorite = { fav -> favorites = if (fav) favorites + pet.id else favorites - pet.id },
                    modifier =
                        Modifier.padding(
                            start = if (index % 2 == 0) Spacing.screenHorizontal else Spacing.none,
                            end = if (index % 2 == 1) Spacing.screenHorizontal else Spacing.none,
                        ),
                )
            }
            item(span = { GridItemSpan(maxLineSpan) }, key = "rows") {
                Surface(
                    modifier = Modifier.padding(horizontal = Spacing.screenHorizontal),
                    shape = MaterialTheme.shapes.medium,
                    color = MaterialTheme.colorScheme.surface,
                ) {
                    Column {
                        Text(
                            text = "List rows",
                            style = MaterialTheme.typography.titleSmall,
                            modifier = Modifier.padding(Spacing.lg),
                        )
                        ListRow(title = "Account", subtitle = "Signed in", icon = PetAdoptIcons.Person, onClick = {})
                        ListRow(title = "Version", subtitle = "0.2.0")
                    }
                }
            }
        }
    }
}

private data class SamplePet(
    val id: String,
    val name: String,
    val subtitle: String,
)

private val SamplePets =
    listOf(
        SamplePet("biscuit", "Biscuit", "Beagle · 2 yrs"),
        SamplePet("miso", "Miso", "Tabby · 8 mos"),
        SamplePet("pepper", "Pepper", "Corgi · 4 yrs"),
        SamplePet("luna", "Luna", "Siamese · 1 yr"),
    )

@PreviewLightDark
@Composable
private fun DesignSystemGalleryPreview() {
    PetAdoptTheme { DesignSystemGallery() }
}
