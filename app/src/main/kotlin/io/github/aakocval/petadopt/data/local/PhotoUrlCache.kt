package io.github.aakocval.petadopt.data.local

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import io.github.aakocval.petadopt.core.common.AppDispatcher
import io.github.aakocval.petadopt.core.common.Dispatcher
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json
import java.io.File
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Remembers which photo URL belongs to which pet (pet id -> URL) in a small JSON file, so photos
 * appear instantly on the next launch, even offline (Coil's disk cache holds the image bytes).
 *
 * Only URLs are cached, never the catalog itself, so catalog updates in the app always win.
 */
@Singleton
class PhotoUrlCache internal constructor(
    private val file: File,
    private val json: Json,
    private val ioDispatcher: CoroutineDispatcher,
) {
    @Inject
    constructor(
        @ApplicationContext context: Context,
        json: Json,
        @Dispatcher(AppDispatcher.IO) ioDispatcher: CoroutineDispatcher,
    ) : this(File(context.filesDir, FILE_NAME), json, ioDispatcher)

    /** Cached URLs, or an empty map if there's no cache yet or it's unreadable. */
    suspend fun read(): Map<String, String> =
        withContext(ioDispatcher) {
            if (!file.exists()) return@withContext emptyMap()
            try {
                json.decodeFromString<Map<String, String>>(file.readText())
            } catch (_: IOException) {
                emptyMap()
            } catch (_: SerializationException) {
                emptyMap() // a corrupt cache is just a cold cache
            }
        }

    /** Writes atomically (temp file + rename), so a crash mid-write can't leave a half file. */
    suspend fun write(urls: Map<String, String>) =
        withContext(ioDispatcher) {
            val temp = File(file.parentFile, "${file.name}.tmp")
            temp.writeText(json.encodeToString(urls))
            if (!temp.renameTo(file)) {
                temp.copyTo(file, overwrite = true)
                temp.delete()
            }
        }

    private companion object {
        const val FILE_NAME = "pet_photo_urls.json"
    }
}
