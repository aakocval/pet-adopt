package io.github.aakocval.petadopt.domain.model

/** A position on Earth. Map-library agnostic, so any MapProvider can project it. */
data class GeoPoint(
    val latitude: Double,
    val longitude: Double,
) {
    init {
        require(latitude in LATITUDE_RANGE) { "latitude out of range: $latitude" }
        require(longitude in LONGITUDE_RANGE) { "longitude out of range: $longitude" }
    }

    private companion object {
        val LATITUDE_RANGE = -90.0..90.0
        val LONGITUDE_RANGE = -180.0..180.0
    }
}
