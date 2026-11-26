package io.github.aakocval.petadopt.di

import kotlinx.coroutines.Dispatchers
import org.junit.jupiter.api.Assertions.assertSame
import org.junit.jupiter.api.Test

/**
 * Guards the one place that maps qualifiers to real dispatchers. A swapped mapping would
 * silently move disk/network work onto the CPU pool (or the reverse).
 */
class DispatchersModuleTest {
    @Test
    fun `IO qualifier provides Dispatchers IO`() {
        assertSame(Dispatchers.IO, DispatchersModule.providesIoDispatcher())
    }

    @Test
    fun `Default qualifier provides Dispatchers Default`() {
        assertSame(Dispatchers.Default, DispatchersModule.providesDefaultDispatcher())
    }
}
