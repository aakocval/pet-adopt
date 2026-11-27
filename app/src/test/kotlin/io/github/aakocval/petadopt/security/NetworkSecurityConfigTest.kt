package io.github.aakocval.petadopt.security

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.w3c.dom.Element
import java.io.File
import java.time.LocalDate
import javax.xml.parsers.DocumentBuilderFactory

/** Guards the shipped network policy, so a careless edit or a forgotten pin rotation fails CI. */
class NetworkSecurityConfigTest {
    private val config: Element =
        DocumentBuilderFactory
            .newInstance()
            .newDocumentBuilder()
            .parse(File("src/main/res/xml/network_security_config.xml"))
            .documentElement

    private fun elements(tag: String) =
        config.getElementsByTagName(tag).let { list ->
            (0 until list.length).map {
                list.item(it) as Element
            }
        }

    @Test
    fun `cleartext is disabled everywhere`() {
        val configs = elements("base-config") + elements("domain-config")
        assertTrue(configs.isNotEmpty())
        assertTrue(configs.all { it.getAttribute("cleartextTrafficPermitted") == "false" })
    }

    @Test
    fun `the pinned host has a primary and a backup pin`() {
        val pins = elements("pin")
        assertTrue(pins.size >= 2, "always ship at least one backup pin")
        assertEquals(pins.size, pins.map { it.textContent.trim() }.toSet().size, "duplicate pins")
    }

    @Test
    fun `pins are rotated well before they expire`() {
        val expiration = LocalDate.parse(elements("pin-set").single().getAttribute("expiration"))
        val deadline = LocalDate.now().plusDays(ROTATION_WARNING_DAYS)
        assertTrue(expiration.isAfter(deadline), "Pins expire $expiration: rotate them now (docs/SECURITY.md)")
    }

    private companion object {
        const val ROTATION_WARNING_DAYS = 60L
    }
}
