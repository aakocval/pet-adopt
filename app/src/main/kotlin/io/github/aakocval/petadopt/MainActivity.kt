package io.github.aakocval.petadopt

import android.content.Intent
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import dagger.hilt.android.AndroidEntryPoint
import io.github.aakocval.petadopt.core.deeplink.DeepLink
import io.github.aakocval.petadopt.core.deeplink.DeepLinkParser
import io.github.aakocval.petadopt.core.designsystem.theme.PetAdoptTheme
import io.github.aakocval.petadopt.feature.explore.map.LocalMapProvider
import io.github.aakocval.petadopt.feature.explore.map.MapProvider
import io.github.aakocval.petadopt.navigation.RootNavHost
import io.github.aakocval.petadopt.ui.RootUiState
import io.github.aakocval.petadopt.ui.RootViewModel
import javax.inject.Inject

/**
 * Single activity. Shows the root graph once onboarding and session state are known.
 * A FragmentActivity only because BiometricPrompt requires one; all UI is still Compose.
 */
@AndroidEntryPoint
class MainActivity : FragmentActivity() {
    private val rootViewModel: RootViewModel by viewModels()

    @Inject
    lateinit var mapProvider: MapProvider

    /** A validated link waiting to be opened. Only the signed-in app consumes it, so it waits behind the auth gate. */
    private var pendingDeepLink by mutableStateOf<DeepLink?>(null)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        // Only on a fresh start: after a rotation the same intent would otherwise reopen the link.
        if (savedInstanceState == null) handle(intent)
        setContent {
            PetAdoptTheme {
                CompositionLocalProvider(LocalMapProvider provides mapProvider) {
                    val state by rootViewModel.uiState.collectAsStateWithLifecycle()
                    // While Loading (a single small DataStore read), the themed window background shows.
                    (state as? RootUiState.Ready)?.let {
                        RootNavHost(
                            destination = it.destination,
                            pendingDeepLink = pendingDeepLink,
                            onDeepLinkHandled = { pendingDeepLink = null },
                        )
                    }
                }
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        handle(intent)
    }

    /** Validate first: anything that isn't an exact, well-formed link is dropped. */
    private fun handle(intent: Intent?) {
        if (intent?.action != Intent.ACTION_VIEW) return
        DeepLinkParser.parse(intent.dataString)?.let { pendingDeepLink = it }
    }
}
