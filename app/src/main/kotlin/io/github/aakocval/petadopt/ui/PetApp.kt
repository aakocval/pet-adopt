package io.github.aakocval.petadopt.ui

import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import io.github.aakocval.petadopt.core.designsystem.component.BottomBarItem
import io.github.aakocval.petadopt.core.designsystem.component.PetAdoptBottomBar
import io.github.aakocval.petadopt.navigation.AppNavHost
import io.github.aakocval.petadopt.navigation.PetAppState
import io.github.aakocval.petadopt.navigation.TopLevelDestination
import io.github.aakocval.petadopt.navigation.rememberPetAppState

/** The app shell: bottom tabs, one snackbar host for every screen, and the nav host. */
@Composable
fun PetApp(appState: PetAppState = rememberPetAppState()) {
    val snackbarHostState = remember { SnackbarHostState() }
    val currentTab = appState.currentTopLevelDestination
    val tabs = TopLevelDestination.entries.map { BottomBarItem(stringResource(it.label), it.icon) }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        snackbarHost = { SnackbarHost(snackbarHostState) },
        bottomBar = {
            if (appState.shouldShowBottomBar) {
                PetAdoptBottomBar(
                    items = tabs,
                    selectedIndex = currentTab?.ordinal ?: -1,
                    onSelect = { index ->
                        val tab = TopLevelDestination.entries[index]
                        if (tab == currentTab) appState.popToRoot(tab) else appState.navigateToTopLevelDestination(tab)
                    },
                )
            }
        },
    ) { padding ->
        AppNavHost(
            appState = appState,
            onShowSnackbar = { message -> snackbarHostState.showSnackbar(message) },
            modifier =
                Modifier
                    .padding(padding)
                    .consumeWindowInsets(padding),
        )
    }
}
