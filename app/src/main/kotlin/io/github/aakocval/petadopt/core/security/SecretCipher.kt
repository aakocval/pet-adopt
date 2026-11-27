package io.github.aakocval.petadopt.core.security

import javax.crypto.Cipher
import javax.crypto.SecretKey
import javax.crypto.spec.GCMParameterSpec

/** Authenticated encryption for small secrets (sessions, tokens). */
interface SecretCipher {
    fun encrypt(plaintext: ByteArray): ByteArray

    /** Throws if [ciphertext] was tampered with or encrypted under a different key. */
    fun decrypt(ciphertext: ByteArray): ByteArray
}

/**
 * AES-256-GCM. Output is `[12-byte IV][ciphertext + 16-byte tag]`; the cipher generates a fresh
 * random IV for every message (Keystore keys enforce that), and GCM's tag detects any tampering.
 *
 * The key comes from a function so production uses an Android Keystore key while JVM tests use an
 * in-memory one with exactly the same code path.
 */
class AesGcmCipher(
    private val key: () -> SecretKey,
) : SecretCipher {
    override fun encrypt(plaintext: ByteArray): ByteArray {
        val cipher = Cipher.getInstance(TRANSFORMATION).apply { init(Cipher.ENCRYPT_MODE, key()) }
        val iv = cipher.iv
        check(iv.size == IV_BYTES) { "unexpected IV size ${iv.size}" }
        return iv + cipher.doFinal(plaintext)
    }

    override fun decrypt(ciphertext: ByteArray): ByteArray {
        require(ciphertext.size > IV_BYTES + TAG_BYTES) { "ciphertext too short" }
        val cipher =
            Cipher.getInstance(TRANSFORMATION).apply {
                init(Cipher.DECRYPT_MODE, key(), GCMParameterSpec(TAG_BYTES * Byte.SIZE_BITS, ciphertext, 0, IV_BYTES))
            }
        return cipher.doFinal(ciphertext, IV_BYTES, ciphertext.size - IV_BYTES)
    }

    private companion object {
        const val TRANSFORMATION = "AES/GCM/NoPadding"
        const val IV_BYTES = 12
        const val TAG_BYTES = 16
    }
}
