package io.github.aakocval.petadopt.feature.profile.ui

import io.github.aakocval.petadopt.core.common.AppInfo
import io.github.aakocval.petadopt.data.repository.InMemoryFavoritesRepository
import io.github.aakocval.petadopt.domain.model.User
import io.github.aakocval.petadopt.domain.repository.AuthRepository
import io.github.aakocval.petadopt.testing.MainDispatcherExtension
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flowOf
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
    private val auth = mockk<AuthRepository> { every { currentUser } returns flowOf(User("Alex", "alex@example.com")) }

    @Test
    fun `shows the user, the app version and a live favorite count`() =
        runTest {
            val viewModel = ProfileViewModel(auth, favorites, AppInfo(versionName = "9.9.9"))
            backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) { viewModel.uiState.collect() }
            assertEquals(
                ProfileUiState(
                    userName = "Alex",
                    userEmail = "alex@example.com",
                    favoriteCount = 0,
                    appVersion = "9.9.9",
                ),
                viewModel.uiState.value,
            )

            favorites.setFavorite("dog-1", isFavorite = true)
            favorites.setFavorite("cat-1", isFavorite = true)

            assertEquals(2, viewModel.uiState.value.favoriteCount)
        }
}
