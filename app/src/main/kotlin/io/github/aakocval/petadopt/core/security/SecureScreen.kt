package io.github.aakocval.petadopt.core.security

import android.view.Window
import android.view.WindowManager
import androidx.activity.compose.LocalActivity
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import java.util.WeakHashMap

/**
 * Marks the window secure while this composable is on screen: no screenshots, no screen
 * recording, and a blank thumbnail in Recents. Use on screens that show account details or PII.
 *
 * Reference-counted per window, so moving between two secure screens (adoption Form -> Review)
 * never clears the flag in between.
 */
@Composable
fun SecureScreen() {
    val window = LocalActivity.current?.window ?: return
    DisposableEffect(window) {
        SecureWindows.acquire(window)
        onDispose { SecureWindows.release(window) }
    }
}

private object SecureWindows {
    private val holders = WeakHashMap<Window, Int>()

    fun acquire(window: Window) {
        val count = holders.getOrDefault(window, 0)
        if (count == 0) window.addFlags(WindowManager.LayoutParams.FLAG_SECURE)
        holders[window] = count + 1
    }

    fun release(window: Window) {
        val count = holders.getOrDefault(window, 0) - 1
        if (count <= 0) {
            holders.remove(window)
            window.clearFlags(WindowManager.LayoutParams.FLAG_SECURE)
        } else {
            holders[window] = count
        }
    }
}
