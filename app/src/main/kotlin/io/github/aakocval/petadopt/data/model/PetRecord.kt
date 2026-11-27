package io.github.aakocval.petadopt.data.model

import io.github.aakocval.petadopt.domain.model.GeoPoint
import io.github.aakocval.petadopt.domain.model.Pet
import io.github.aakocval.petadopt.domain.model.Sex
import io.github.aakocval.petadopt.domain.model.Species
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * How the data layer stores a pet: flat, serializable, and carrying data-only details the domain
 * doesn't need ([photoQuery], which tells the remote source what to fetch). Mapped to [Pet] at the
 * repository boundary.
 */
@Serializable
data class PetRecord(
    val id: String,
    val name: String,
    val species: SpeciesRecord,
    val breed: String,
    val ageInMonths: Int,
    val sex: SexRecord,
    val bio: String,
    val shelterName: String,
    val latitude: Double,
    val longitude: Double,
    /** Dog CEO breed path (e.g. "collie/border"); `null` for cats, which use Cataas ids. */
    val photoQuery: String? = null,
    val photoUrl: String? = null,
)

@Serializable
enum class SpeciesRecord {
    @SerialName("dog")
    Dog,

    @SerialName("cat")
    Cat,
}

@Serializable
enum class SexRecord {
    @SerialName("male")
    Male,

    @SerialName("female")
    Female,
}

fun PetRecord.toDomain() =
    Pet(
        id = id,
        name = name,
        species = if (species == SpeciesRecord.Dog) Species.Dog else Species.Cat,
        breed = breed,
        ageInMonths = ageInMonths,
        sex = if (sex == SexRecord.Male) Sex.Male else Sex.Female,
        bio = bio,
        shelterName = shelterName,
        location = GeoPoint(latitude, longitude),
        photoUrl = photoUrl,
    )
