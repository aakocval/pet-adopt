package io.github.aakocval.petadopt.core.security

import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import java.security.KeyStore
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey

/**
 * Keys that live in the Android Keystore: generated inside it, usable for encryption, never
 * exportable, and backed by secure hardware (TEE/StrongBox) on devices that have it.
 * Replaces the deprecated androidx.security:security-crypto MasterKey.
 */
object KeystoreKeys {
    private const val PROVIDER = "AndroidKeyStore"
    private const val KEY_BITS = 256

    fun getOrCreateAesKey(alias: String): SecretKey {
        val keyStore = KeyStore.getInstance(PROVIDER).apply { load(null) }
        (keyStore.getKey(alias, null) as? SecretKey)?.let { return it }

        val spec =
            KeyGenParameterSpec
                .Builder(alias, KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT)
                .setBlockModes(KeyProperties.BLOCK_MODE_GCM)
                .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
                .setKeySize(KEY_BITS)
                .setRandomizedEncryptionRequired(true) // the caller can't choose (and reuse) an IV
                .build()
        return KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES, PROVIDER).apply { init(spec) }.generateKey()
    }
}
