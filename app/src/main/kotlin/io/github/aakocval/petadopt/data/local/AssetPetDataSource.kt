package io.github.aakocval.petadopt.data.local

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import io.github.aakocval.petadopt.data.model.PetRecord
import kotlinx.serialization.json.Json
import javax.inject.Inject

/** Names, ages and bios ship with the app (`assets/pets.json`), so the catalog never needs a network. */
class AssetPetDataSource
    @Inject
    constructor(
        @param:ApplicationContext private val context: Context,
        private val json: Json,
    ) {
        /** Blocking I/O: call from an IO dispatcher. */
        fun load(): List<PetRecord> =
            context.assets
                .open(ASSET)
                .bufferedReader()
                .use { parsePetCatalog(json, it.readText()) }

        private companion object {
            const val ASSET = "pets.json"
        }
    }
