package io.github.aakocval.petadopt.feature.adoption.ui

import androidx.activity.compose.BackHandler
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import io.github.aakocval.petadopt.core.security.SecureScreen
import io.github.aakocval.petadopt.core.ui.ObserveAsEvents

/*
 * Stateful wrappers for the three steps. They all receive the same graph-scoped ViewModel, react
 * only to the events that concern their step, and forward navigation to the graph.
 */

@Composable
internal fun AdoptionFormRoute(
    viewModel: AdoptionViewModel,
    onBack: () -> Unit,
    onShowReview: () -> Unit,
    modifier: Modifier = Modifier,
) {
    SecureScreen() // name, email, phone and address details
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    ObserveAsEvents(viewModel.events) { if (it == AdoptionEvent.ShowReview) onShowReview() }
    val actions =
        remember(viewModel, onBack) {
            AdoptionFormActions(
                onFullNameChange = viewModel::onFullNameChange,
                onEmailChange = viewModel::onEmailChange,
                onPhoneChange = viewModel::onPhoneChange,
                onHomeTypeSelect = viewModel::onHomeTypeSelect,
                onHasOtherPetsChange = viewModel::onHasOtherPetsChange,
                onMessageChange = viewModel::onMessageChange,
                onHomeVisitChange = viewModel::onHomeVisitChange,
                onContinue = viewModel::onContinue,
                onBack = onBack,
            )
        }
    AdoptionFormScreen(uiState = uiState, actions = actions, modifier = modifier)
}

@Composable
internal fun AdoptionReviewRoute(
    viewModel: AdoptionViewModel,
    onEdit: () -> Unit,
    onSubmitted: () -> Unit,
    modifier: Modifier = Modifier,
) {
    SecureScreen() // name, email, phone and address details
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    ObserveAsEvents(viewModel.events) { if (it == AdoptionEvent.Submitted) onSubmitted() }
    AdoptionReviewScreen(uiState = uiState, onEdit = onEdit, onSubmit = viewModel::onSubmit, modifier = modifier)
}

@Composable
internal fun AdoptionSuccessRoute(
    viewModel: AdoptionViewModel,
    onDone: () -> Unit,
    modifier: Modifier = Modifier,
) {
    SecureScreen() // name, email, phone and address details
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    // System Back from Success leaves the whole flow, same as Done.
    BackHandler(onBack = onDone)
    AdoptionSuccessScreen(uiState = uiState, onDone = onDone, modifier = modifier)
}
