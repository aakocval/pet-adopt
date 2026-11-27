package io.github.aakocval.petadopt.feature.adoption.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.dropUnlessResumed
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import androidx.navigation.toRoute
import io.github.aakocval.petadopt.feature.adoption.ui.AdoptionFormRoute
import io.github.aakocval.petadopt.feature.adoption.ui.AdoptionReviewRoute
import io.github.aakocval.petadopt.feature.adoption.ui.AdoptionSuccessRoute
import io.github.aakocval.petadopt.feature.adoption.ui.AdoptionViewModel
import kotlinx.serialization.Serializable

/** The whole flow, keyed by the pet being applied for. Its back stack entry owns the shared ViewModel. */
@Serializable
data class AdoptionGraph(
    val petId: String,
)

@Serializable
data object AdoptionFormDestination

@Serializable
data object AdoptionReviewDestination

@Serializable
data object AdoptionSuccessDestination

fun NavController.navigateToAdoption(petId: String) = navigate(AdoptionGraph(petId)) { launchSingleTop = true }

/**
 * Form -> Review -> Success as one nested graph.
 *
 * Unlike other features, this one takes the [NavController]: it needs the graph's own back stack
 * entry to scope the shared ViewModel, and the steps are internal to the flow. Entering is still a
 * lambda-free call from outside (`navigateToAdoption`), and it never navigates to other features.
 */
fun NavGraphBuilder.adoptionGraph(navController: NavController) {
    navigation<AdoptionGraph>(startDestination = AdoptionFormDestination) {
        composable<AdoptionFormDestination> { entry ->
            AdoptionFormRoute(
                viewModel = entry.adoptionViewModel(navController),
                onBack = dropUnlessResumed { navController.popBackStack() },
                onShowReview = { navController.navigate(AdoptionReviewDestination) { launchSingleTop = true } },
            )
        }
        composable<AdoptionReviewDestination> { entry ->
            AdoptionReviewRoute(
                viewModel = entry.adoptionViewModel(navController),
                onEdit = dropUnlessResumed { navController.popBackStack() },
                onSubmitted = {
                    navController.navigate(AdoptionSuccessDestination) {
                        // A sent application can't be edited: drop Form and Review from the stack.
                        popUpTo<AdoptionFormDestination> { inclusive = true }
                    }
                },
            )
        }
        composable<AdoptionSuccessDestination> { entry ->
            AdoptionSuccessRoute(
                viewModel = entry.adoptionViewModel(navController),
                onDone = dropUnlessResumed { navController.popBackStack<AdoptionGraph>(inclusive = true) },
            )
        }
    }
}

/**
 * The ViewModel scoped to the [AdoptionGraph] entry rather than to this screen, so every step gets
 * the same instance and it's cleared when the flow is popped.
 */
@Composable
private fun NavBackStackEntry.adoptionViewModel(navController: NavController): AdoptionViewModel {
    val graphEntry = remember(this) { navController.getBackStackEntry<AdoptionGraph>() }
    val petId = remember(graphEntry) { graphEntry.toRoute<AdoptionGraph>().petId }
    return hiltViewModel<AdoptionViewModel, AdoptionViewModel.Factory>(
        viewModelStoreOwner = graphEntry,
        creationCallback = { factory -> factory.create(petId) },
    )
}
