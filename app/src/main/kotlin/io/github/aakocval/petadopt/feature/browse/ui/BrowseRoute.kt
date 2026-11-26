package io.github.aakocval.petadopt.feature.browse.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalResources
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import io.github.aakocval.petadopt.R
import io.github.aakocval.petadopt.core.ui.ObserveAsEvents

/**
 * Stateful half of the Browse screen: gets the ViewModel, collects state with lifecycle
 * awareness, turns events into snackbars and forwards navigation to the caller.
 * Everything visual lives in [BrowseScreen].
 */
@Composable
fun BrowseRoute(
    onPetClick: (petId: String) -> Unit,
    onShowSnackbar: suspend (message: String) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: BrowseViewModel = hiltViewModel(),
) {
    // Stops collecting when the app goes to the background; WhileSubscribed(5s) then pauses upstream.
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val resources = LocalResources.current

    ObserveAsEvents(viewModel.events) { event ->
        when (event) {
            is BrowseEvent.FavoriteChanged -> {
                onShowSnackbar(
                    resources.getString(
                        if (event.isFavorite) R.string.browse_favorite_added else R.string.browse_favorite_removed,
                        event.petName,
                    ),
                )
            }
        }
    }

    BrowseScreen(
        uiState = uiState,
        onFilterSelected = viewModel::onFilterSelected,
        onPetClick = onPetClick,
        onToggleFavorite = viewModel::onToggleFavorite,
        onRetry = viewModel::onRetry,
        modifier = modifier,
    )
}
