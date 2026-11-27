package io.github.aakocval.petadopt.domain.model

/**
 * A pet available for adoption. Pure Kotlin: the domain layer has no Android or framework
 * imports, so it can be tested on the JVM and reused anywhere.
 *
 * Ages are stored in months so "8 months" and "2 years" are both exact; formatting is a UI concern.
 */
data class Pet(
    val id: String,
    val name: String,
    val species: Species,
    val breed: String,
    val ageInMonths: Int,
    val sex: Sex,
    val bio: String,
    val shelterName: String,
    /** Where the pet is waiting (its shelter), for the Explore map. */
    val location: GeoPoint,
    /** A remote photo, or `null` until one is fetched (the UI shows an illustrated face meanwhile). */
    val photoUrl: String? = null,
) {
    init {
        require(id.isNotBlank()) { "Pet id must not be blank" }
        require(ageInMonths >= 0) { "Pet age must not be negative" }
    }
}

enum class Species { Dog, Cat }

enum class Sex { Male, Female }
