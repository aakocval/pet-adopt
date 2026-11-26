package io.github.aakocval.petadopt.core.designsystem.theme

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource

class TileColorsTest {
    @ParameterizedTest
    @ValueSource(strings = ["", "dog-1", "cat-42", "a very long pet id with spaces", "Ωmega"])
    fun `index is always within bounds`(key: String) {
        val index = tileIndexFor(key, tileCount = 6)
        assertTrue(index in 0 until 6, "index $index out of bounds for '$key'")
    }

    @Test
    fun `same key always maps to the same index`() {
        assertEquals(tileIndexFor("dog-1", 6), tileIndexFor("dog-1", 6))
    }

    @Test
    fun `negative hash codes still map into bounds`() {
        // "polygenelubricants".hashCode() == Int.MIN_VALUE, the classic abs() trap.
        assertTrue(tileIndexFor("polygenelubricants", 6) in 0 until 6)
    }

    @Test
    fun `keys spread across tiles`() {
        val used = (1..60).map { tileIndexFor("pet-$it", 6) }.toSet()
        assertEquals(6, used.size)
    }

    @Test
    fun `zero tiles is rejected`() {
        assertThrows<IllegalArgumentException> { tileIndexFor("dog-1", 0) }
    }
}
