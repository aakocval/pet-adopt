package io.github.aakocval.petadopt.core.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import coil3.compose.AsyncImage
import io.github.aakocval.petadopt.core.designsystem.component.PetPlaceholder
import io.github.aakocval.petadopt.core.designsystem.icon.PetAdoptIcons
import io.github.aakocval.petadopt.domain.model.Species

/**
 * A pet's photo over its illustrated face. The face is always drawn first, so while the photo
 * loads, if it fails, or when there's no network, the face shows instead of an empty box.
 * Coil sizes the request to this layout and crossfades the photo in (configured app-wide).
 */
@Composable
fun PetPhoto(
    photoUrl: String?,
    species: Species,
    modifier: Modifier = Modifier,
) {
    Box(modifier = modifier.fillMaxSize()) {
        PetPlaceholder(face = if (species == Species.Dog) PetAdoptIcons.DogFace else PetAdoptIcons.CatFace)
        if (photoUrl != null) {
            AsyncImage(
                model = photoUrl,
                contentDescription = null, // the pet's name is right next to it
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize(),
            )
        }
    }
}
