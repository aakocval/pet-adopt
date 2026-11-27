package io.github.aakocval.petadopt.testing

import io.github.aakocval.petadopt.core.security.AesGcmCipher
import javax.crypto.KeyGenerator

/** The production AES-GCM code with an in-memory key instead of the Android Keystore. */
fun testCipher(): AesGcmCipher {
    val key = KeyGenerator.getInstance("AES").apply { init(256) }.generateKey()
    return AesGcmCipher { key }
}
