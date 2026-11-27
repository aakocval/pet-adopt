package io.github.aakocval.petadopt.core.ui

import io.github.aakocval.petadopt.domain.model.FavoritablePet
import io.github.aakocval.petadopt.domain.model.Species
import io.github.aakocval.petadopt.testing.testPet
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

/** Regression: species and photo were once dropped here and silently defaulted (cats got dog faces, no photos). */
class PetCardUiTest {
    @Test
    fun `maps every field the tile needs`() {
        val pet = testPet(id = "cat-1", name = "Mia", species = Species.Cat).copy(photoUrl = "https://cataas.com/cat/x")

        val card = FavoritablePet(pet, isFavorite = true).toCardUi()

        assertEquals(PetCardUi("cat-1", "Mia", "Mixed", 24, true, Species.Cat, "https://cataas.com/cat/x"), card)
    }
}
