package io.github.aakocval.petadopt.feature.profile.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import io.github.aakocval.petadopt.R
import io.github.aakocval.petadopt.core.security.SecureScreen
import io.github.aakocval.petadopt.core.security.rememberBiometricGate

@Composable
fun ProfileRoute(
    onOpenFavorites: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: ProfileViewModel = hiltViewModel(),
) {
    // Shows the account email: keep it out of screenshots and Recents.
    SecureScreen()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    // Masked by default; revealing it requires re-authentication when the device has a screen
    // lock. Plain remember (not rememberSaveable): it's masked again after rotation or a restart.
    var isEmailRevealed by remember { mutableStateOf(false) }
    val gate = rememberBiometricGate()
    val revealTitle = stringResource(R.string.profile_reveal_title)

    ProfileScreen(
        uiState = uiState,
        onOpenFavorites = onOpenFavorites,
        onSignOut = viewModel::onSignOut,
        isEmailRevealed = isEmailRevealed,
        onToggleEmail = {
            when {
                isEmailRevealed -> isEmailRevealed = false
                gate?.isAvailable() == true -> gate.authenticate(revealTitle) { isEmailRevealed = true }
                else -> isEmailRevealed = true // no screen lock: nothing to verify against
            }
        },
        modifier = modifier,
    )
}
