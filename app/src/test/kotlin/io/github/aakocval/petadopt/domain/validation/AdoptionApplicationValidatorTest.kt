package io.github.aakocval.petadopt.domain.validation

import io.github.aakocval.petadopt.domain.model.HomeType
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource

class AdoptionApplicationValidatorTest {
    private val valid =
        ApplicationDraft(
            fullName = "Alex Morgan",
            email = "alex@example.com",
            phone = "+1 (555) 010-2030",
            homeType = HomeType.House,
            hasOtherPets = true,
            message = "We have a big garden and love long walks.",
            agreesToHomeVisit = true,
        )

    private fun errorFor(
        field: ApplicationField,
        draft: ApplicationDraft,
    ) = AdoptionApplicationValidator.validate(draft)[field]

    @Test
    fun `a complete draft has no errors`() {
        assertTrue(AdoptionApplicationValidator.validate(valid).isEmpty())
    }

    @Test
    fun `an empty draft reports every required field`() {
        val errors = AdoptionApplicationValidator.validate(ApplicationDraft())
        assertEquals(ApplicationField.entries.toSet(), errors.keys)
        assertTrue(errors.values.all { it == FieldError.Required })
    }

    @Nested
    inner class FullName {
        @ParameterizedTest
        @ValueSource(strings = ["Zoë Brontë", "Jean-Luc O'Neil", "李 小龙", "Dr. Mae Jemison"])
        fun `accepts names in any script with common punctuation`(name: String) {
            assertNull(errorFor(ApplicationField.FullName, valid.copy(fullName = name)))
        }

        @ParameterizedTest
        @ValueSource(strings = ["<script>", "Robert'); DROP TABLE", "Alex 2", "a@b"])
        fun `rejects digits and markup characters`(name: String) {
            assertEquals(FieldError.InvalidCharacters, errorFor(ApplicationField.FullName, valid.copy(fullName = name)))
        }

        @Test
        fun `needs at least two letters`() {
            assertEquals(FieldError.TooShort, errorFor(ApplicationField.FullName, valid.copy(fullName = " A. ")))
        }

        @Test
        fun `whitespace only counts as missing`() {
            assertEquals(FieldError.Required, errorFor(ApplicationField.FullName, valid.copy(fullName = "   ")))
        }
    }

    @Nested
    inner class Phone {
        @ParameterizedTest
        @ValueSource(strings = ["5550102", "+44 20 7946 0958", "(555) 010-2030"])
        fun `accepts common formats`(phone: String) {
            assertNull(errorFor(ApplicationField.Phone, valid.copy(phone = phone)))
        }

        @Test
        fun `too few digits`() {
            assertEquals(FieldError.TooShort, errorFor(ApplicationField.Phone, valid.copy(phone = "555-01")))
        }

        @Test
        fun `too many digits for E164`() {
            assertEquals(FieldError.TooLong, errorFor(ApplicationField.Phone, valid.copy(phone = "1234567890123456")))
        }

        @Test
        fun `plus only allowed as a prefix`() {
            assertEquals(FieldError.InvalidFormat, errorFor(ApplicationField.Phone, valid.copy(phone = "555+0102030")))
        }
    }

    @Test
    fun `message must be meaningful after trimming`() {
        val padded = "   too short   " + " ".repeat(20)
        assertEquals(FieldError.TooShort, errorFor(ApplicationField.Message, valid.copy(message = padded)))
    }

    @Test
    fun `home visit consent is required`() {
        assertEquals(FieldError.Required, errorFor(ApplicationField.HomeVisit, valid.copy(agreesToHomeVisit = false)))
    }

    @Test
    fun `toApplication normalizes the draft`() {
        val application =
            AdoptionApplicationValidator.toApplication(
                petId = "dog-1",
                draft =
                    valid.copy(
                        fullName = "  Alex    Morgan ",
                        email = " alex@example.com ",
                        message = "  ${valid.message}  ",
                    ),
            )
        assertNotNull(application)
        assertEquals("Alex Morgan", application!!.fullName)
        assertEquals("alex@example.com", application.email)
        assertEquals("+15550102030", application.phone)
        assertEquals(valid.message, application.message)
    }

    @Test
    fun `toApplication refuses an invalid draft`() {
        assertNull(AdoptionApplicationValidator.toApplication("dog-1", valid.copy(email = "nope")))
    }
}
