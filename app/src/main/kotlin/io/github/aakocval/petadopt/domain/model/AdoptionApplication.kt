package io.github.aakocval.petadopt.domain.model

/** A validated, normalized application. Only [AdoptionApplicationValidator] creates these. */
data class AdoptionApplication(
    val petId: String,
    val fullName: String,
    val email: String,
    val phone: String,
    val homeType: HomeType,
    val hasOtherPets: Boolean,
    val message: String,
)

enum class HomeType { House, Apartment, Other }
