package io.github.aakocval.petadopt.feature.adoption.ui

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.aakocval.petadopt.domain.model.HomeType
import io.github.aakocval.petadopt.domain.repository.AdoptionRepository
import io.github.aakocval.petadopt.domain.repository.AuthRepository
import io.github.aakocval.petadopt.domain.repository.PetRepository
import io.github.aakocval.petadopt.domain.repository.SubmitResult
import io.github.aakocval.petadopt.domain.validation.AdoptionApplicationValidator
import io.github.aakocval.petadopt.domain.validation.ApplicationDraft
import io.github.aakocval.petadopt.domain.validation.ApplicationInputSanitizer
import kotlinx.collections.immutable.persistentMapOf
import kotlinx.collections.immutable.toImmutableMap
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * State for the whole adoption flow (Form -> Review -> Success).
 *
 * One instance is scoped to the adoption nav graph, so all three screens share it and it's
 * cleared when the flow is left. The pet id comes in through assisted injection; the draft is
 * written through to [SavedStateHandle] so it survives process death.
 */
@HiltViewModel(assistedFactory = AdoptionViewModel.Factory::class)
class AdoptionViewModel
    @AssistedInject
    constructor(
        @Assisted private val petId: String,
        private val savedStateHandle: SavedStateHandle,
        petRepository: PetRepository,
        authRepository: AuthRepository,
        private val adoptionRepository: AdoptionRepository,
    ) : ViewModel() {
        private val draft = MutableStateFlow(savedStateHandle.restoreDraft())
        private val showErrors = MutableStateFlow(false)
        private val submission = MutableStateFlow(Submission(reference = savedStateHandle[KEY_REFERENCE]))

        private val eventChannel = Channel<AdoptionEvent>(Channel.BUFFERED)
        val events: Flow<AdoptionEvent> = eventChannel.receiveAsFlow()

        val uiState: StateFlow<AdoptionUiState> =
            combine(
                petRepository.observePet(petId).map { it?.name },
                draft,
                showErrors,
                submission,
            ) { petName, draft, showErrors, submission ->
                AdoptionUiState(
                    petName = petName,
                    draft = draft,
                    errors =
                        if (showErrors) {
                            AdoptionApplicationValidator
                                .validate(
                                    draft,
                                ).toImmutableMap()
                        } else {
                            persistentMapOf()
                        },
                    isSubmitting = submission.inFlight,
                    submitFailed = submission.failed,
                    reference = submission.reference,
                )
            }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(STOP_TIMEOUT_MILLIS), AdoptionUiState())

        init {
            // Prefill from the account, but never overwrite a restored draft.
            viewModelScope.launch {
                val user = authRepository.currentUser.first() ?: return@launch
                if (draft.value.fullName.isEmpty() && draft.value.email.isEmpty()) {
                    updateDraft { it.copy(fullName = user.displayName, email = user.email) }
                }
            }
        }

        fun onFullNameChange(value: String) = updateDraft { it.copy(fullName = ApplicationInputSanitizer.name(value)) }

        fun onEmailChange(value: String) = updateDraft { it.copy(email = ApplicationInputSanitizer.email(value)) }

        fun onPhoneChange(value: String) = updateDraft { it.copy(phone = ApplicationInputSanitizer.phone(value)) }

        fun onHomeTypeSelect(value: HomeType) = updateDraft { it.copy(homeType = value) }

        fun onHasOtherPetsChange(value: Boolean) = updateDraft { it.copy(hasOtherPets = value) }

        fun onMessageChange(value: String) = updateDraft { it.copy(message = ApplicationInputSanitizer.message(value)) }

        fun onHomeVisitChange(value: Boolean) = updateDraft { it.copy(agreesToHomeVisit = value) }

        /** Form -> Review, only when everything is valid. Errors become visible from now on. */
        fun onContinue() {
            showErrors.value = true
            if (AdoptionApplicationValidator.validate(draft.value).isEmpty()) {
                eventChannel.trySend(AdoptionEvent.ShowReview)
            }
        }

        /** Review -> Success. Validates again: never trust that the form screen did. */
        fun onSubmit() {
            if (submission.value.inFlight || submission.value.reference != null) return
            val application =
                AdoptionApplicationValidator.toApplication(petId, draft.value) ?: run {
                    showErrors.value = true
                    return
                }
            submission.value = Submission(inFlight = true)
            viewModelScope.launch {
                when (val result = adoptionRepository.submit(application)) {
                    is SubmitResult.Success -> {
                        savedStateHandle[KEY_REFERENCE] = result.reference
                        submission.value = Submission(reference = result.reference)
                        eventChannel.send(AdoptionEvent.Submitted)
                    }

                    SubmitResult.Failure -> {
                        submission.value = Submission(failed = true)
                    }
                }
            }
        }

        private fun updateDraft(transform: (ApplicationDraft) -> ApplicationDraft) {
            draft.update(transform)
            savedStateHandle.saveDraft(draft.value)
            submission.update { it.copy(failed = false) }
        }

        private data class Submission(
            val inFlight: Boolean = false,
            val failed: Boolean = false,
            val reference: String? = null,
        )

        @AssistedFactory
        interface Factory {
            fun create(petId: String): AdoptionViewModel
        }

        private companion object {
            const val STOP_TIMEOUT_MILLIS = 5_000L
        }
    }
