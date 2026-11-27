package io.github.aakocval.petadopt.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStoreFile
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import io.github.aakocval.petadopt.core.common.AppDispatcher
import io.github.aakocval.petadopt.core.common.Dispatcher
import io.github.aakocval.petadopt.data.local.LegacySessionMigration
import io.github.aakocval.petadopt.data.local.SessionCodec
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DataStoreModule {
    /**
     * One preferences file. It's created lazily on first read (not in Application.onCreate), does
     * its I/O on the injected IO dispatcher, and is excluded from cloud backup and device transfer
     * by `data_extraction_rules.xml`. Plain values are non-sensitive; the session is stored
     * encrypted (SecureSessionStore), and old plaintext sessions are migrated on first read.
     */
    @Provides
    @Singleton
    fun providesPreferencesDataStore(
        @ApplicationContext context: Context,
        @Dispatcher(AppDispatcher.IO) ioDispatcher: CoroutineDispatcher,
        sessionCodec: SessionCodec,
    ): DataStore<Preferences> =
        PreferenceDataStoreFactory.create(
            migrations = listOf(LegacySessionMigration(sessionCodec)),
            scope = CoroutineScope(ioDispatcher + SupervisorJob()),
            produceFile = { context.preferencesDataStoreFile("user_preferences") },
        )
}
