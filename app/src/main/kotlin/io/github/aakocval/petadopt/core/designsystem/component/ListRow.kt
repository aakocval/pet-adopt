package io.github.aakocval.petadopt.core.designsystem.component

import androidx.annotation.DrawableRes
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import io.github.aakocval.petadopt.core.designsystem.icon.PetAdoptIcons
import io.github.aakocval.petadopt.core.designsystem.theme.PetAdoptTheme
import io.github.aakocval.petadopt.core.designsystem.theme.Spacing

/**
 * Settings-style row: optional icon in a pastel rounded square, title, optional subtitle and
 * a chevron when the row navigates somewhere. Pass `onClick = null` for a static row.
 */
@Composable
fun ListRow(
    title: String,
    modifier: Modifier = Modifier,
    subtitle: String? = null,
    @DrawableRes icon: Int? = null,
    iconBackground: Color = PetAdoptTheme.colors.tiles[0],
    onClick: (() -> Unit)? = null,
) {
    Row(
        modifier =
            modifier
                .fillMaxWidth()
                .then(if (onClick != null) Modifier.clickable(role = Role.Button, onClick = onClick) else Modifier)
                .heightIn(min = 56.dp)
                .padding(horizontal = Spacing.lg, vertical = Spacing.sm),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        if (icon != null) {
            Box(
                modifier =
                    Modifier
                        .size(32.dp)
                        .background(iconBackground, MaterialTheme.shapes.small),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    painter = painterResource(icon),
                    contentDescription = null, // the title describes the row
                    tint = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.size(18.dp),
                )
            }
            Spacer(Modifier.width(Spacing.md))
        }
        Column(modifier = Modifier.weight(1f)) {
            Text(text = title, style = MaterialTheme.typography.bodyLarge, color = MaterialTheme.colorScheme.onSurface)
            if (subtitle != null) {
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
        if (onClick != null) {
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
private fun ListRowPreview() {
    PetAdoptTheme {
        Surface(color = MaterialTheme.colorScheme.surface) {
            Column {
                ListRow(title = "Account", subtitle = "Signed in", icon = PetAdoptIcons.Person, onClick = {})
                HorizontalDivider(modifier = Modifier.padding(start = 60.dp), color = MaterialTheme.colorScheme.outline)
                ListRow(
                    title = "Saved pets",
                    icon = PetAdoptIcons.Heart,
                    iconBackground = PetAdoptTheme.colors.tiles[5],
                    onClick = {},
                )
                HorizontalDivider(modifier = Modifier.padding(start = 60.dp), color = MaterialTheme.colorScheme.outline)
                ListRow(title = "Version", subtitle = "0.2.0")
            }
        }
    }
}
