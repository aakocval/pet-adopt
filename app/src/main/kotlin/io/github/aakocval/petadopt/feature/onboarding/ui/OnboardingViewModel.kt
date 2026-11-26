package io.github.aakocval.petadopt.feature.onboarding.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.aakocval.petadopt.domain.repository.UserPreferencesRepository
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Onboarding only records that it's done. It doesn't navigate: the app root observes the
 * preference and swaps the onboarding graph for the auth graph, clearing the back stack.
 * Which page is showing is plain UI state (the pager), so it isn't here.
 */
@HiltViewModel
class OnboardingViewModel
    @Inject
    constructor(
        private val userPreferences: UserPreferencesRepository,
    ) : ViewModel() {
        fun onFinish() {
            viewModelScope.launch { userPreferences.setOnboardingCompleted() }
        }
    }
