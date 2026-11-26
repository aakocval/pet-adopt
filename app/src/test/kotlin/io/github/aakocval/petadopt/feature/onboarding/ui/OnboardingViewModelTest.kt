package io.github.aakocval.petadopt.feature.onboarding.ui

import io.github.aakocval.petadopt.domain.repository.UserPreferencesRepository
import io.github.aakocval.petadopt.testing.MainDispatcherExtension
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.RegisterExtension

class OnboardingViewModelTest {
    @JvmField
    @RegisterExtension
    val mainDispatcher = MainDispatcherExtension()

    @Test
    fun `finishing marks onboarding as completed`() {
        val preferences = mockk<UserPreferencesRepository> { coEvery { setOnboardingCompleted() } returns Unit }

        OnboardingViewModel(preferences).onFinish()

        coVerify(exactly = 1) { preferences.setOnboardingCompleted() }
    }
}
