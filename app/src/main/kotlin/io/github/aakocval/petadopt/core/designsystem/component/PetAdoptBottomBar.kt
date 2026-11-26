package io.github.aakocval.petadopt.core.designsystem.component

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import io.github.aakocval.petadopt.core.designsystem.icon.PetAdoptIcons
import io.github.aakocval.petadopt.core.designsystem.theme.PetAdoptTheme

/**
 * iOS-like tab bar on top of Material's NavigationBar (which keeps the accessibility semantics):
 * flat surface, hairline top divider, no pill indicator, accent tint for the selected tab.
 */
@Composable
fun PetAdoptBottomBar(
    items: List<BottomBarItem>,
    selectedIndex: Int,
    onSelect: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier) {
        HorizontalDivider(thickness = 0.5.dp, color = MaterialTheme.colorScheme.outline)
        NavigationBar(containerColor = MaterialTheme.colorScheme.surface, tonalElevation = 0.dp) {
            items.forEachIndexed { index, item ->
                NavigationBarItem(
                    selected = index == selectedIndex,
                    onClick = { onSelect(index) },
                    icon = { Icon(painter = painterResource(item.icon), contentDescription = null) },
                    label = { Text(text = item.label, style = MaterialTheme.typography.labelMedium) },
                    colors =
                        NavigationBarItemDefaults.colors(
                            selectedIconColor = MaterialTheme.colorScheme.primary,
                            selectedTextColor = MaterialTheme.colorScheme.primary,
                            unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                            unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant,
                            indicatorColor = Color.Transparent,
                        ),
                )
            }
        }
    }
}

@PreviewLightDark
@Composable
private fun PetAdoptBottomBarPreview() {
    PetAdoptTheme {
        PetAdoptBottomBar(
            items =
                listOf(
                    BottomBarItem("Browse", PetAdoptIcons.Paw),
                    BottomBarItem("Explore", PetAdoptIcons.MapPin),
                    BottomBarItem("Favorites", PetAdoptIcons.Heart),
                    BottomBarItem("Profile", PetAdoptIcons.Person),
                ),
            selectedIndex = 0,
            onSelect = {},
        )
    }
}
