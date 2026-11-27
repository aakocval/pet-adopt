package io.github.aakocval.petadopt.data.local

import io.github.aakocval.petadopt.data.model.PetRecord
import io.github.aakocval.petadopt.data.model.SexRecord
import io.github.aakocval.petadopt.data.model.SpeciesRecord
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

/** Shape of `assets/pets.json`: shelters once, pets referencing them by id. */
@Serializable
internal data class PetCatalog(
    val shelters: List<ShelterEntry>,
    val pets: List<PetEntry>,
)

@Serializable
internal data class ShelterEntry(
    val id: String,
    val name: String,
    val latitude: Double,
    val longitude: Double,
)

@Serializable
internal data class PetEntry(
    val id: String,
    val name: String,
    val species: SpeciesRecord,
    val breed: String,
    val ageInMonths: Int,
    val sex: SexRecord,
    val bio: String,
    val shelterId: String,
    val latitude: Double,
    val longitude: Double,
    val photoQuery: String? = null,
)

/** Parses the bundled catalog. A pet pointing at an unknown shelter is a data bug, so it fails loudly. */
internal fun parsePetCatalog(
    json: Json,
    text: String,
): List<PetRecord> {
    val catalog = json.decodeFromString<PetCatalog>(text)
    val shelters = catalog.shelters.associateBy { it.id }
    return catalog.pets.map { pet ->
        val shelter =
            requireNotNull(shelters[pet.shelterId]) { "Pet ${pet.id} references unknown shelter ${pet.shelterId}" }
        PetRecord(
            id = pet.id,
            name = pet.name,
            species = pet.species,
            breed = pet.breed,
            ageInMonths = pet.ageInMonths,
            sex = pet.sex,
            bio = pet.bio,
            shelterName = shelter.name,
            latitude = pet.latitude,
            longitude = pet.longitude,
            photoQuery = pet.photoQuery,
        )
    }
}
