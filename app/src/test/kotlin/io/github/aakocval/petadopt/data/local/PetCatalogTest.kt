package io.github.aakocval.petadopt.data.local

import io.github.aakocval.petadopt.data.model.SpeciesRecord
import io.github.aakocval.petadopt.data.model.toDomain
import kotlinx.serialization.json.Json
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.io.File

class PetCatalogTest {
    private val json = Json { ignoreUnknownKeys = true }

    /** Unit tests run with the module as working directory, so this is the asset the app ships. */
    private val shippedCatalog = File("src/main/assets/pets.json").readText()

    @Test
    fun `the shipped catalog parses into valid domain pets`() {
        val records = parsePetCatalog(json, shippedCatalog)
        val pets = records.map { it.toDomain() } // throws if any coordinate or age is invalid

        assertEquals(12, pets.size)
        assertEquals(pets.size, pets.map { it.id }.toSet().size, "duplicate ids")
        assertTrue(pets.all { it.photoUrl == null }, "photos come from the network, never the asset")
    }

    @Test
    fun `every dog has a photo query and no cat does`() {
        val records = parsePetCatalog(json, shippedCatalog)
        assertTrue(records.filter { it.species == SpeciesRecord.Dog }.all { !it.photoQuery.isNullOrBlank() })
        assertTrue(records.filter { it.species == SpeciesRecord.Cat }.all { it.photoQuery == null })
    }

    @Test
    fun `shelter names are resolved from ids`() {
        val text =
            """
            {"shelters":[{"id":"s1","name":"Test Shelter","latitude":1.0,"longitude":2.0}],
             "pets":[{"id":"p1","name":"Rex","species":"dog","breed":"Mixed","ageInMonths":3,"sex":"male",
                      "bio":"Hi","shelterId":"s1","latitude":1.0,"longitude":2.0,"photoQuery":"mix","extra":"ignored"}]}
            """.trimIndent()
        assertEquals("Test Shelter", parsePetCatalog(json, text).single().shelterName)
    }

    @Test
    fun `a pet with an unknown shelter fails loudly`() {
        val text =
            """
            {"shelters":[],"pets":[{"id":"p1","name":"Rex","species":"dog","breed":"Mixed","ageInMonths":3,
             "sex":"male","bio":"Hi","shelterId":"missing","latitude":1.0,"longitude":2.0}]}
            """.trimIndent()
        assertThrows<IllegalArgumentException> { parsePetCatalog(json, text) }
    }
}
