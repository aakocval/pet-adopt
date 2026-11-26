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
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.receiveAsFlow
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
        // One synchronously updated state. A text field must see its new value in the same frame it
        // reports it; deriving the state through combine() adds async hops that scramble fast typing.
        private val state =
            MutableStateFlow(
                AdoptionUiState(draft = savedStateHandle.restoreDraft(), reference = savedStateHandle[KEY_REFERENCE]),
            )
        val uiState: StateFlow<AdoptionUiState> = state.asStateFlow()

        /** Errors stay hidden until the first Continue; afterwards they track every edit. */
        private var showErrors = false

        private val eventChannel = Channel<AdoptionEvent>(Channel.BUFFERED)
        val events: Flow<AdoptionEvent> = eventChannel.receiveAsFlow()

        init {
            petRepository
                .observePet(petId)
                .map { it?.name }
                .onEach { name -> state.update { it.copy(petName = name) } }
                .launchIn(viewModelScope)

            // Prefill from the account, but never overwrite a restored draft.
            viewModelScope.launch {
                val user = authRepository.currentUser.first() ?: return@launch
                if (state.value.draft.fullName
                        .isEmpty() &&
                    state.value.draft.email
                        .isEmpty()
                ) {
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
            showErrors = true
            val errors = AdoptionApplicationValidator.validate(state.value.draft)
            state.update { it.copy(errors = errors.toImmutableMap()) }
            if (errors.isEmpty()) eventChannel.trySend(AdoptionEvent.ShowReview)
        }

        /** Review -> Success. Validates again: never trust that the form screen did. */
        fun onSubmit() {
            val current = state.value
            if (current.isSubmitting || current.reference != null) return
            val application =
                AdoptionApplicationValidator.toApplication(petId, current.draft) ?: run {
                    showErrors = true
                    state.update { it.copy(errors = AdoptionApplicationValidator.validate(it.draft).toImmutableMap()) }
                    return
                }
            state.update { it.copy(isSubmitting = true, submitFailed = false) }
            viewModelScope.launch {
                when (val result = adoptionRepository.submit(application)) {
                    is SubmitResult.Success -> {
                        savedStateHandle[KEY_REFERENCE] = result.reference
                        state.update { it.copy(isSubmitting = false, reference = result.reference) }
                        eventChannel.send(AdoptionEvent.Submitted)
                    }

                    SubmitResult.Failure -> {
                        state.update { it.copy(isSubmitting = false, submitFailed = true) }
                    }
                }
            }
        }

        private fun updateDraft(transform: (ApplicationDraft) -> ApplicationDraft) {
            state.update { current ->
                val draft = transform(current.draft)
                current.copy(
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
                    submitFailed = false,
                )
            }
            savedStateHandle.saveDraft(state.value.draft)
        }

        @AssistedFactory
        interface Factory {
            fun create(petId: String): AdoptionViewModel
        }
    }
