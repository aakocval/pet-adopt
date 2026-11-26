package io.github.aakocval.petadopt

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import dagger.hilt.android.AndroidEntryPoint
import io.github.aakocval.petadopt.core.designsystem.theme.PetAdoptTheme
import io.github.aakocval.petadopt.navigation.RootNavHost
import io.github.aakocval.petadopt.ui.RootUiState
import io.github.aakocval.petadopt.ui.RootViewModel

/** Single activity. Shows the root graph once onboarding and session state are known. */
@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private val rootViewModel: RootViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            PetAdoptTheme {
                val state by rootViewModel.uiState.collectAsStateWithLifecycle()
                // While Loading (a single small DataStore read), the themed window background shows.
                (state as? RootUiState.Ready)?.let { RootNavHost(destination = it.destination) }
            }
        }
    }
}
