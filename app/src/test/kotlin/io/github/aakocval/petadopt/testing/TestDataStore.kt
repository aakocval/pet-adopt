package io.github.aakocval.petadopt.testing

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import kotlinx.coroutines.test.TestScope
import java.io.File

/** A real Preferences DataStore in a temp directory, scoped to the test. */
fun TestScope.testPreferencesDataStore(directory: File): DataStore<Preferences> =
    PreferenceDataStoreFactory.create(
        scope = backgroundScope,
        produceFile = { File(directory, "test.preferences_pb") },
    )
