package io.github.aakocval.petadopt.core.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.repeatOnLifecycle
import kotlinx.coroutines.flow.Flow

/**
 * Collects one-off ViewModel events (snackbars, haptics) only while the screen is at least
 * STARTED. Events sent while the app is in the background wait in the ViewModel's buffered
 * channel instead of being shown to nobody.
 */
@Composable
fun <T> ObserveAsEvents(
    events: Flow<T>,
    onEvent: suspend (T) -> Unit,
) {
    val lifecycleOwner = LocalLifecycleOwner.current
    // Always call the latest lambda without restarting collection when it changes.
    val currentOnEvent by rememberUpdatedState(onEvent)
    LaunchedEffect(events, lifecycleOwner) {
        lifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
            events.collect { currentOnEvent(it) }
        }
    }
}
