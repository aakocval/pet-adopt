package io.github.aakocval.petadopt.domain.validation

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class ApplicationInputSanitizerTest {
    @Test
    fun `name keeps spaces while typing but drops control characters and caps length`() {
        assertEquals("Alex ", ApplicationInputSanitizer.name("Alex "))
        assertEquals("AlexMorgan", ApplicationInputSanitizer.name("Alex\u0000Morgan\u001B"))
        assertEquals(
            AdoptionApplicationValidator.NAME_MAX_LENGTH,
            ApplicationInputSanitizer.name("a".repeat(500)).length,
        )
    }

    @Test
    fun `email drops all whitespace`() {
        assertEquals("alex@example.com", ApplicationInputSanitizer.email(" alex @example.com\t"))
    }

    @Test
    fun `phone keeps digits and dialing symbols only`() {
        assertEquals("+1 (555) 010-2030", ApplicationInputSanitizer.phone("+1 (555) 010-2030abc#*."))
        assertEquals(
            ApplicationInputSanitizer.PHONE_MAX_LENGTH,
            ApplicationInputSanitizer.phone("1".repeat(100)).length,
        )
    }

    @Test
    fun `message keeps line breaks but drops other control characters`() {
        assertEquals("Line one\nLine two", ApplicationInputSanitizer.message("Line one\n\u0007Line two\u0000"))
    }
}
