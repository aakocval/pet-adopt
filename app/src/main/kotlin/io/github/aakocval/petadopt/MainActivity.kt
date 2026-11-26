package io.github.aakocval.petadopt

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import dagger.hilt.android.AndroidEntryPoint
import io.github.aakocval.petadopt.core.designsystem.DesignSystemGallery
import io.github.aakocval.petadopt.core.designsystem.theme.PetAdoptTheme

/**
 * Single activity. Hosts the Compose tree inside [PetAdoptTheme]; navigation plugs in here in M3.
 */
@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            PetAdoptTheme {
                DesignSystemGallery()
            }
        }
    }
}
