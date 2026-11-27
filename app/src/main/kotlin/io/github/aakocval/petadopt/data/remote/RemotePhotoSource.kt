package io.github.aakocval.petadopt.data.remote

import javax.inject.Inject

/**
 * Finds photo URLs for pets. Everything the APIs return is treated as untrusted: paths are
 * allow-listed before use, and only HTTPS URLs on the expected image hosts are accepted.
 *
 * Network and parsing failures are thrown to the caller, which decides how to degrade.
 */
class RemotePhotoSource
    @Inject
    constructor(
        private val dogApi: DogCeoApi,
        private val catApi: CataasApi,
    ) {
        /** A random photo for a Dog CEO breed path such as "beagle" or "collie/border", or `null`. */
        suspend fun dogPhoto(breedPath: String): String? {
            if (!breedPathPattern.matches(breedPath)) return null
            val response = dogApi.randomImage(breedPath)
            return response.message.takeIf { response.status == "success" && it.startsWith(DOG_IMAGE_HOST) }
        }

        /** Up to [count] stable cat photo URLs, sized for tiles. */
        suspend fun catPhotos(count: Int): List<String> =
            catApi
                .cats(limit = count)
                .map { it.id }
                .filter { catIdPattern.matches(it) }
                .map { "$CAT_IMAGE_BASE$it?width=$CAT_IMAGE_WIDTH" }

        internal companion object {
            const val DOG_IMAGE_HOST = "https://images.dog.ceo/"
            const val CAT_IMAGE_BASE = "https://cataas.com/cat/"

            /** Wide enough for a full-width detail photo; Coil downsamples further for tiles. */
            const val CAT_IMAGE_WIDTH = 800

            private val breedPathPattern = Regex("^[a-z]+(/[a-z]+)?$")
            private val catIdPattern = Regex("^[A-Za-z0-9-]{1,64}$")
        }
    }
