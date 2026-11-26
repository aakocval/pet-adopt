package io.github.aakocval.petadopt.testing

import io.github.aakocval.petadopt.domain.model.Pet
import io.github.aakocval.petadopt.domain.model.Sex
import io.github.aakocval.petadopt.domain.model.Species

/** Small, readable fixtures. Tests state only what they care about. */
fun testPet(
    id: String = "dog-1",
    name: String = "Rex",
    species: Species = Species.Dog,
    ageInMonths: Int = 24,
) = Pet(
    id = id,
    name = name,
    species = species,
    breed = "Mixed",
    ageInMonths = ageInMonths,
    sex = Sex.Male,
    bio = "A good pet.",
    shelterName = "Test Shelter",
)
