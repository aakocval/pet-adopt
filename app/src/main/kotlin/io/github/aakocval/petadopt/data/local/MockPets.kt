package io.github.aakocval.petadopt.data.local

import io.github.aakocval.petadopt.domain.model.Pet
import io.github.aakocval.petadopt.domain.model.Sex
import io.github.aakocval.petadopt.domain.model.Species

/**
 * Seed data for the in-memory repository. Fictional pets and shelters.
 * Moves to a bundled JSON asset in M6, when photos start coming from the network.
 */
internal object MockPets {
    val all: List<Pet> =
        listOf(
            Pet(
                id = "dog-biscuit",
                name = "Biscuit",
                species = Species.Dog,
                breed = "Beagle",
                ageInMonths = 26,
                sex = Sex.Male,
                bio = "Nose-first explorer who loves long sniffy walks and a warm lap afterwards.",
                shelterName = "Harbor Paws Rescue",
            ),
            Pet(
                id = "cat-miso",
                name = "Miso",
                species = Species.Cat,
                breed = "Tabby",
                ageInMonths = 8,
                sex = Sex.Female,
                bio = "Curious kitten who will supervise everything you do, especially cooking.",
                shelterName = "Maple Street Shelter",
            ),
            Pet(
                id = "dog-pepper",
                name = "Pepper",
                species = Species.Dog,
                breed = "Corgi",
                ageInMonths = 50,
                sex = Sex.Female,
                bio = "Short legs, big opinions. Great with kids and very good at fetch.",
                shelterName = "Harbor Paws Rescue",
            ),
            Pet(
                id = "cat-luna",
                name = "Luna",
                species = Species.Cat,
                breed = "Siamese",
                ageInMonths = 14,
                sex = Sex.Female,
                bio = "Chatty and affectionate. Will tell you all about her day.",
                shelterName = "Second Chance Cats",
            ),
            Pet(
                id = "dog-otis",
                name = "Otis",
                species = Species.Dog,
                breed = "Labrador",
                ageInMonths = 72,
                sex = Sex.Male,
                bio = "Gentle giant, house-trained, happiest snoozing at your feet.",
                shelterName = "Greenfield Animal Haven",
            ),
            Pet(
                id = "cat-tofu",
                name = "Tofu",
                species = Species.Cat,
                breed = "Ragdoll",
                ageInMonths = 36,
                sex = Sex.Male,
                bio = "Goes limp when picked up. Professional napper, amateur bird watcher.",
                shelterName = "Maple Street Shelter",
            ),
            Pet(
                id = "dog-maple",
                name = "Maple",
                species = Species.Dog,
                breed = "Shiba Inu",
                ageInMonths = 18,
                sex = Sex.Female,
                bio = "Independent and clever. Needs a patient human and a secure yard.",
                shelterName = "Greenfield Animal Haven",
            ),
            Pet(
                id = "cat-pixel",
                name = "Pixel",
                species = Species.Cat,
                breed = "Tuxedo",
                ageInMonths = 5,
                sex = Sex.Male,
                bio = "Tiny, fast and fearless. Best paired with another playful cat.",
                shelterName = "Second Chance Cats",
            ),
            Pet(
                id = "dog-juniper",
                name = "Juniper",
                species = Species.Dog,
                breed = "Border Collie",
                ageInMonths = 30,
                sex = Sex.Female,
                bio = "Smart and energetic. Would love an active home and a job to do.",
                shelterName = "Harbor Paws Rescue",
            ),
            Pet(
                id = "cat-olive",
                name = "Olive",
                species = Species.Cat,
                breed = "Calico",
                ageInMonths = 96,
                sex = Sex.Female,
                bio = "Calm senior who wants a sunny windowsill and quiet company.",
                shelterName = "Maple Street Shelter",
            ),
            Pet(
                id = "dog-waffles",
                name = "Waffles",
                species = Species.Dog,
                breed = "Dachshund",
                ageInMonths = 10,
                sex = Sex.Male,
                bio = "Loves blankets, burrowing and everyone he meets.",
                shelterName = "Greenfield Animal Haven",
            ),
            Pet(
                id = "cat-sage",
                name = "Sage",
                species = Species.Cat,
                breed = "Russian Blue",
                ageInMonths = 42,
                sex = Sex.Male,
                bio = "Shy at first, then your shadow. Gets along with dogs.",
                shelterName = "Second Chance Cats",
            ),
        )
}
