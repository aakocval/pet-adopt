package io.github.aakocval.petadopt.data.local

import androidx.datastore.core.DataMigration
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.stringPreferencesKey
import io.github.aakocval.petadopt.core.security.SecretCipher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.serialization.Serializable
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json
import java.io.IOException
import java.security.GeneralSecurityException
import java.security.SecureRandom
import java.util.Base64
import javax.inject.Inject
import javax.inject.Singleton

/** Everything about the signed-in session. Only ever written to disk encrypted. */
@Serializable
data class StoredSession(
    val displayName: String,
    val email: String,
    val accessToken: String,
)

/** Session <-> encrypted Base64 string. Shared by the store and the legacy migration. */
class SessionCodec
    @Inject
    constructor(
        private val cipher: SecretCipher,
        private val json: Json,
    ) {
        fun encode(session: StoredSession): String =
            Base64.getEncoder().encodeToString(cipher.encrypt(json.encodeToString(session).toByteArray()))

        /** `null` if the value is corrupt or the Keystore key is gone (e.g. after a device restore). */
        fun decode(value: String): StoredSession? =
            try {
                json.decodeFromString<StoredSession>(String(cipher.decrypt(Base64.getDecoder().decode(value))))
            } catch (_: GeneralSecurityException) {
                null
            } catch (_: IllegalArgumentException) {
                null
            } catch (_: SerializationException) {
                null
            }
    }

/**
 * The session, encrypted with a Keystore-backed key and stored as one opaque value in DataStore.
 * Anyone reading the preferences file (a backup tool, a rooted device) sees ciphertext only.
 */
@Singleton
class SecureSessionStore
    @Inject
    constructor(
        private val dataStore: DataStore<Preferences>,
        private val codec: SessionCodec,
    ) {
        val session: Flow<StoredSession?> =
            dataStore.data
                .catch { error -> if (error is IOException) emit(emptyPreferences()) else throw error }
                .map { prefs -> prefs[EncryptedSession]?.let(codec::decode) }
                .distinctUntilChanged()

        suspend fun save(session: StoredSession) {
            dataStore.edit { it[EncryptedSession] = codec.encode(session) }
        }

        suspend fun clear() {
            dataStore.edit { it.remove(EncryptedSession) }
        }

        internal companion object {
            val EncryptedSession = stringPreferencesKey("session_encrypted_v1")
        }
    }

/**
 * Upgrades installs from v0.5-0.7, which kept the session email and name in plain DataStore keys:
 * re-saves them encrypted (with a fresh token) and deletes the plaintext. Runs once, before the
 * first read, so the user stays signed in and no plaintext copy survives.
 */
class LegacySessionMigration(
    private val codec: SessionCodec,
) : DataMigration<Preferences> {
    override suspend fun shouldMigrate(currentData: Preferences) =
        LegacyEmail in currentData || LegacyName in currentData

    override suspend fun migrate(currentData: Preferences): Preferences {
        val migrated = currentData.toMutablePreferences()
        val email = currentData[LegacyEmail]
        if (email != null) {
            val name = currentData[LegacyName] ?: email.substringBefore('@')
            migrated[SecureSessionStore.EncryptedSession] =
                codec.encode(StoredSession(name, email, FakeTokens.newToken()))
        }
        migrated.remove(LegacyEmail)
        migrated.remove(LegacyName)
        return migrated.toPreferences()
    }

    override suspend fun cleanUp() = Unit

    internal companion object {
        val LegacyEmail = stringPreferencesKey("session_email")
        val LegacyName = stringPreferencesKey("session_display_name")
    }
}

/** Opaque random tokens for the fake backend (a real one would issue them). */
object FakeTokens {
    private const val TOKEN_BYTES = 32
    private val random = SecureRandom()

    fun newToken(): String = ByteArray(TOKEN_BYTES).also(random::nextBytes).joinToString("") { "%02x".format(it) }
}
