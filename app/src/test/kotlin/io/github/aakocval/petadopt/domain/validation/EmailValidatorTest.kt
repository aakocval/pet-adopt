package io.github.aakocval.petadopt.domain.validation

import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource

class EmailValidatorTest {
    @ParameterizedTest
    @ValueSource(strings = ["alex@example.com", "a.b+tag@sub.example.org", "x@y.io"])
    fun `accepts well-formed addresses`(email: String) {
        assertTrue(EmailValidator.isValid(email))
    }

    @ParameterizedTest
    @ValueSource(
        strings = [
            "",
            "alex",
            "alex@",
            "@example.com",
            "alex@example",
            "a b@example.com",
            "a@@example.com",
            "alex@example.c",
        ],
    )
    fun `rejects malformed addresses`(email: String) {
        assertFalse(EmailValidator.isValid(email))
    }

    @Test
    fun `rejects addresses over the length limit`() {
        val tooLong = "a".repeat(EmailValidator.MAX_LENGTH) + "@example.com"
        assertFalse(EmailValidator.isValid(tooLong))
    }
}
