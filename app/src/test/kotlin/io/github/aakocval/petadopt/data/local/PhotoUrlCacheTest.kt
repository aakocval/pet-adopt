package io.github.aakocval.petadopt.data.local

import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.json.Json
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir
import java.io.File

class PhotoUrlCacheTest {
    @TempDir
    lateinit var directory: File

    private fun cache(file: File = File(directory, "urls.json")) = PhotoUrlCache(file, Json, UnconfinedTestDispatcher())

    @Test
    fun `no file means an empty cache`() =
        runTest {
            assertEquals(emptyMap<String, String>(), cache().read())
        }

    @Test
    fun `round trips and survives a new instance`() =
        runTest {
            cache().write(mapOf("dog-1" to "https://images.dog.ceo/a.jpg"))
            assertEquals(mapOf("dog-1" to "https://images.dog.ceo/a.jpg"), cache().read())
        }

    @Test
    fun `a corrupt file is treated as a cold cache`() =
        runTest {
            val file = File(directory, "urls.json").apply { writeText("{not json") }
            assertEquals(emptyMap<String, String>(), cache(file).read())
        }
}
