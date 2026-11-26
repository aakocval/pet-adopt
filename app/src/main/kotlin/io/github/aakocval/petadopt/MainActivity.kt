package io.github.aakocval.petadopt

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import dagger.hilt.android.AndroidEntryPoint
import io.github.aakocval.petadopt.core.designsystem.theme.PetAdoptTheme
import io.github.aakocval.petadopt.feature.browse.navigation.BrowseGraph
import io.github.aakocval.petadopt.feature.browse.navigation.browseGraph

/**
 * Single activity. Hosts the Compose tree inside [PetAdoptTheme].
 *
 * For now it wires one feature graph directly; M3 moves this into an AppNavHost with bottom
 * navigation and per-tab back stacks.
 */
@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            PetAdoptTheme {
                val navController = rememberNavController()
                val snackbarHostState = remember { SnackbarHostState() }
                Scaffold(
                    containerColor = MaterialTheme.colorScheme.background,
                    snackbarHost = { SnackbarHost(snackbarHostState) },
                ) { padding ->
                    NavHost(
                        navController = navController,
                        startDestination = BrowseGraph,
                        modifier = Modifier.padding(padding),
                    ) {
                        browseGraph(
                            onPetClick = { /* Pet Detail arrives in M3. */ },
                            onShowSnackbar = { message -> snackbarHostState.showSnackbar(message) },
                        )
                    }
                }
            }
        }
    }
}
