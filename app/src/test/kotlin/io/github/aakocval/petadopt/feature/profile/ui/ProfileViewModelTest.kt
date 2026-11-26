package io.github.aakocval.petadopt.feature.profile.ui

import io.github.aakocval.petadopt.core.common.AppInfo
import io.github.aakocval.petadopt.data.repository.InMemoryFavoritesRepository
import io.github.aakocval.petadopt.testing.MainDispatcherExtension
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.RegisterExtension

class ProfileViewModelTest {
    @JvmField
    @RegisterExtension
    val mainDispatcher = MainDispatcherExtension()

    private val favorites = InMemoryFavoritesRepository()

    @Test
    fun `shows the app version and a live favorite count`() =
        runTest {
            val viewModel = ProfileViewModel(favorites, AppInfo(versionName = "9.9.9"))
            backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) { viewModel.uiState.collect() }
            assertEquals(ProfileUiState(favoriteCount = 0, appVersion = "9.9.9"), viewModel.uiState.value)

            favorites.setFavorite("dog-1", isFavorite = true)
            favorites.setFavorite("cat-1", isFavorite = true)

            assertEquals(2, viewModel.uiState.value.favoriteCount)
        }
}
