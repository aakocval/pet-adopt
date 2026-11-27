package io.github.aakocval.petadopt.core.deeplink

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource

class DeepLinkParserTest {
    @ParameterizedTest
    @ValueSource(
        strings = ["petadopt://pet/dog-biscuit", "petadopt://pet/cat-miso/", "petadopt://pet/dog-biscuit?ref=share"],
    )
    fun `accepts well-formed pet links`(uri: String) {
        assertEquals(
            DeepLink.Pet(uri.substringAfter("pet/").substringBefore('/').substringBefore('?')),
            DeepLinkParser.parse(uri),
        )
    }

    @ParameterizedTest
    @ValueSource(
        strings = [
            "", // empty
            "https://pet/dog-biscuit", // wrong scheme
            "petadopt://admin/dog-biscuit", // wrong host
            "petadopt://user:pass@pet/dog-biscuit", // credentials
            "petadopt://pet:8080/dog-biscuit", // port
            "petadopt://pet/../settings", // traversal
            "petadopt://pet/dog-biscuit/apply", // extra segment
            "petadopt://pet/DOG-BISCUIT", // wrong id format
            "petadopt://pet/dog-<script>", // markup
            "petadopt://pet/lizard-rex", // unknown species prefix
            "petadopt://pet/%64og-biscuit", // encoded tricks
            "petadopt://pet/", // no id
            "not a uri at all",
        ],
    )
    fun `rejects everything else`(uri: String) {
        assertNull(DeepLinkParser.parse(uri))
    }

    @Test
    fun `rejects null and oversized input`() {
        assertNull(DeepLinkParser.parse(null))
        assertNull(DeepLinkParser.parse("petadopt://pet/dog-" + "a".repeat(200)))
    }
}
