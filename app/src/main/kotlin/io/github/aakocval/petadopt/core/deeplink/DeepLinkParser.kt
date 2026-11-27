package io.github.aakocval.petadopt.core.deeplink

import java.net.URI
import java.net.URISyntaxException

/** A deep link the app knows how to act on. */
sealed interface DeepLink {
    data class Pet(
        val petId: String,
    ) : DeepLink
}

/**
 * Turns an incoming URI into a [DeepLink], or `null`. Deep links are attacker-controlled input
 * (any app or web page can fire one), so this is an allow-list: exact scheme, exact host, no
 * credentials or port, one path segment in the exact id format. Everything else is ignored,
 * never "best-effort" parsed.
 */
object DeepLinkParser {
    private const val SCHEME = "petadopt"
    private const val HOST_PET = "pet"
    private const val MAX_LENGTH = 128
    private val petIdPattern = Regex("^(dog|cat)-[a-z]{1,32}$")

    fun parse(uri: String?): DeepLink? =
        uri
            ?.takeIf { it.length <= MAX_LENGTH }
            ?.let(::toUriOrNull)
            ?.takeIf(::isOurPetLink)
            ?.let(::singlePathSegment)
            ?.takeIf { petIdPattern.matches(it) }
            ?.let { DeepLink.Pet(it) }

    private fun toUriOrNull(value: String): URI? =
        try {
            URI(value)
        } catch (_: URISyntaxException) {
            null
        }

    private fun isOurPetLink(uri: URI): Boolean {
        val expectedTarget = uri.scheme == SCHEME && uri.host == HOST_PET
        val noExtras = uri.userInfo == null && uri.port == -1
        return expectedTarget && noExtras
    }

    private fun singlePathSegment(uri: URI): String? =
        uri.path
            .orEmpty()
            .trim('/')
            .split('/')
            .singleOrNull()
}
