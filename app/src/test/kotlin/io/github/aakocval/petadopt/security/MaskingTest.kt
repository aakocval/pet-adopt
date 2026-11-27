package io.github.aakocval.petadopt.security

import io.github.aakocval.petadopt.core.security.maskEmail
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource

class MaskingTest {
    @ParameterizedTest
    @CsvSource(
        "alex.morgan@example.com, a••••••••@example.com", // long local part: capped at 8 dots
        "jo@example.com, j•@example.com",
        "a@example.com, a•@example.com", // single character still gets one dot
        "not-an-email, ••••••••",
        "@example.com, ••••••••",
    )
    fun `hides the local part but keeps the domain`(
        email: String,
        expected: String,
    ) {
        assertEquals(expected, maskEmail(email))
    }
}
