package io.github.aakocval.petadopt.feature.profile.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import io.github.aakocval.petadopt.core.security.SecureScreen

@Composable
fun ProfileRoute(
    onOpenFavorites: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: ProfileViewModel = hiltViewModel(),
) {
    // Shows the account email: keep it out of screenshots and Recents.
    SecureScreen()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    ProfileScreen(
        uiState = uiState,
        onOpenFavorites = onOpenFavorites,
        onSignOut = viewModel::onSignOut,
        modifier = modifier,
    )
}
