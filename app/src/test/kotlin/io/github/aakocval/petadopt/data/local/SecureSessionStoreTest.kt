package io.github.aakocval.petadopt.data.local

import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.mutablePreferencesOf
import io.github.aakocval.petadopt.testing.testCipher
import io.github.aakocval.petadopt.testing.testPreferencesDataStore
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.json.Json
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir
import java.io.File

class SecureSessionStoreTest {
    @TempDir
    lateinit var directory: File

    private val codec = SessionCodec(testCipher(), Json)
    private val session =
        StoredSession(displayName = "Alex", email = "alex@example.com", accessToken = "tok_secret_123")

    @Test
    fun `round trips a session`() =
        runTest {
            val store = SecureSessionStore(testPreferencesDataStore(directory), codec)
            store.save(session)
            assertEquals(session, store.session.first())

            store.clear()
            assertNull(store.session.first())
        }

    @Test
    fun `nothing readable reaches the file on disk`() =
        runTest {
            SecureSessionStore(testPreferencesDataStore(directory), codec).save(session)

            val onDisk = File(directory, "test.preferences_pb").readBytes().toString(Charsets.ISO_8859_1)
            assertFalse("alex@example.com" in onDisk, "email stored in plaintext")
            assertFalse("tok_secret_123" in onDisk, "token stored in plaintext")
        }

    @Test
    fun `a corrupt value means signed out, not a crash`() =
        runTest {
            val dataStore = testPreferencesDataStore(directory)
            dataStore.edit { it[SecureSessionStore.EncryptedSession] = "not-base64-ciphertext!" }
            assertNull(SecureSessionStore(dataStore, codec).session.first())
        }

    @Test
    fun `a session encrypted under another key, as after a device restore, means signed out`() =
        runTest {
            val dataStore = testPreferencesDataStore(directory)
            dataStore.edit {
                it[SecureSessionStore.EncryptedSession] =
                    SessionCodec(
                        testCipher(),
                        Json,
                    ).encode(session)
            }
            assertNull(SecureSessionStore(dataStore, codec).session.first())
        }

    @Test
    fun `an old plaintext session is migrated to encrypted and the plaintext is deleted`() =
        runTest {
            val legacy =
                mutablePreferencesOf(
                    LegacySessionMigration.LegacyEmail to "alex@example.com",
                    LegacySessionMigration.LegacyName to "Alex",
                )
            val migration = LegacySessionMigration(codec)
            assertTrue(migration.shouldMigrate(legacy))

            val migrated = migration.migrate(legacy)

            assertNull(migrated[LegacySessionMigration.LegacyEmail])
            assertNull(migrated[LegacySessionMigration.LegacyName])
            val restored = codec.decode(requireNotNull(migrated[SecureSessionStore.EncryptedSession]))
            assertEquals("alex@example.com", restored?.email)
            assertEquals("Alex", restored?.displayName)
        }

    @Test
    fun `installs without a plaintext session are left alone`() =
        runTest {
            assertFalse(LegacySessionMigration(codec).shouldMigrate(mutablePreferencesOf()))
        }
}
