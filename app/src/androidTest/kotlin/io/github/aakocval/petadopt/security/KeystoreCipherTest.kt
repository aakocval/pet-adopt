package io.github.aakocval.petadopt.security

import androidx.test.ext.junit.runners.AndroidJUnit4
import io.github.aakocval.petadopt.core.security.AesGcmCipher
import io.github.aakocval.petadopt.core.security.KeystoreKeys
import org.junit.Assert.assertArrayEquals
import org.junit.Assert.assertSame
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import java.security.KeyStore
import javax.crypto.AEADBadTagException

/** Runs against the real Android Keystore on a device or emulator. */
@RunWith(AndroidJUnit4::class)
class KeystoreCipherTest {
    private val alias = "test_key_${System.nanoTime()}"
    private val cipher = AesGcmCipher { KeystoreKeys.getOrCreateAesKey(alias) }

    @Test
    fun roundTripsWithAKeystoreKey() {
        val secret = "session".toByteArray()
        assertArrayEquals(secret, cipher.decrypt(cipher.encrypt(secret)))
    }

    @Test
    fun theKeyIsCreatedOnceAndReused() {
        cipher.encrypt(byteArrayOf(1))
        val keyStore = KeyStore.getInstance("AndroidKeyStore").apply { load(null) }
        assertTrue(keyStore.containsAlias(alias))
        assertSame(keyStore.getKey(alias, null)::class, KeystoreKeys.getOrCreateAesKey(alias)::class)
    }

    @Test(expected = AEADBadTagException::class)
    fun tamperingIsDetected() {
        val ciphertext = cipher.encrypt("session".toByteArray())
        ciphertext[ciphertext.size - 1] = (ciphertext.last() + 1).toByte()
        cipher.decrypt(ciphertext)
    }
}
