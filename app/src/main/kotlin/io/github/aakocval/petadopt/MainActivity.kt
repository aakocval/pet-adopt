package io.github.aakocval.petadopt

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import dagger.hilt.android.AndroidEntryPoint
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
                PlaceholderScreen()
            }
        }
    }
}

@Composable
private fun PlaceholderScreen() {
    Surface(modifier = Modifier.fillMaxSize()) {
        Box(contentAlignment = Alignment.Center) {
            Text(text = "Pet Adopt", style = MaterialTheme.typography.headlineLarge)
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun PlaceholderScreenPreview() {
    PetAdoptTheme { PlaceholderScreen() }
}
