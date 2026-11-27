package io.github.aakocval.petadopt.security

import io.github.aakocval.petadopt.testing.testCipher
import org.junit.jupiter.api.Assertions.assertArrayEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.security.GeneralSecurityException

class AesGcmCipherTest {
    private val cipher = testCipher()
    private val secret = "token-123".toByteArray()

    @Test
    fun `round trips`() {
        assertArrayEquals(secret, cipher.decrypt(cipher.encrypt(secret)))
    }

    @Test
    fun `the same plaintext encrypts differently every time (fresh IV)`() {
        assertFalse(cipher.encrypt(secret).contentEquals(cipher.encrypt(secret)))
    }

    @Test
    fun `tampering is detected`() {
        val ciphertext = cipher.encrypt(secret).also { it[it.size - 1] = (it.last() + 1).toByte() }
        assertThrows<GeneralSecurityException> { cipher.decrypt(ciphertext) }
    }

    @Test
    fun `a different key can't decrypt`() {
        assertThrows<GeneralSecurityException> { testCipher().decrypt(cipher.encrypt(secret)) }
    }
}
