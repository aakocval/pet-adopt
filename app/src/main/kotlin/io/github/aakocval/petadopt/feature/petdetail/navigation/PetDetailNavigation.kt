package io.github.aakocval.petadopt.feature.petdetail.navigation

import androidx.lifecycle.compose.dropUnlessResumed
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import io.github.aakocval.petadopt.feature.petdetail.ui.PetDetailRoute
import kotlinx.serialization.Serializable

/** Type-safe route: the compiler guarantees a pet id is always passed. */
@Serializable
data class PetDetailDestination(
    val petId: String,
)

fun NavController.navigateToPetDetail(petId: String) =
    navigate(PetDetailDestination(petId)) {
        // Tapping the same pet twice quickly must not stack two copies.
        launchSingleTop = true
    }

/**
 * Adds Pet Detail to whichever graph calls it. The app registers it inside each tab that can open
 * a pet (Browse, Favorites), so the detail is pushed onto that tab's own back stack.
 */
fun NavGraphBuilder.petDetailScreen(onBack: () -> Unit) {
    composable<PetDetailDestination> { entry ->
        PetDetailRoute(
            petId = entry.toRoute<PetDetailDestination>().petId,
            // Ignore taps while this screen is animating out: a fast double tap on Back would
            // otherwise pop twice and remove the tab's root screen too.
            onBack = dropUnlessResumed(block = onBack),
        )
    }
}
