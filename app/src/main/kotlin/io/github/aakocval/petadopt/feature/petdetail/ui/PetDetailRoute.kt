package io.github.aakocval.petadopt.feature.petdetail.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle

/** Stateful Pet Detail: builds the ViewModel for [petId] and forwards back navigation. */
@Composable
fun PetDetailRoute(
    petId: String,
    onBack: () -> Unit,
    onApply: (petId: String) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: PetDetailViewModel =
        hiltViewModel<PetDetailViewModel, PetDetailViewModel.Factory>(
            creationCallback = { factory -> factory.create(petId) },
        ),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    PetDetailScreen(
        uiState = uiState,
        onBack = onBack,
        onToggleFavorite = viewModel::onToggleFavorite,
        onApply = { onApply(petId) },
        modifier = modifier,
    )
}
