package io.github.aakocval.petadopt.feature.explore.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@Composable
fun ExploreRoute(
    onPetClick: (petId: String) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: ExploreViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    ExploreScreen(
        uiState = uiState,
        onPinSelected = viewModel::onPinSelected,
        onPetClick = onPetClick,
        modifier = modifier,
    )
}
