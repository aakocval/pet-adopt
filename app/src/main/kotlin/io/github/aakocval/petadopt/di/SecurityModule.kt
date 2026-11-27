package io.github.aakocval.petadopt.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.github.aakocval.petadopt.core.security.AesGcmCipher
import io.github.aakocval.petadopt.core.security.KeystoreKeys
import io.github.aakocval.petadopt.core.security.SecretCipher
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object SecurityModule {
    private const val SESSION_KEY_ALIAS = "petadopt_session_key"

    /** The Keystore key is looked up on first use, not at injection time (keeps startup light). */
    @Provides
    @Singleton
    fun providesSecretCipher(): SecretCipher {
        val key by lazy { KeystoreKeys.getOrCreateAesKey(SESSION_KEY_ALIAS) }
        return AesGcmCipher { key }
    }
}
