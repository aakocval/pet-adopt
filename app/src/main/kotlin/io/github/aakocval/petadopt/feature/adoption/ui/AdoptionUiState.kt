package io.github.aakocval.petadopt.feature.adoption.ui

import io.github.aakocval.petadopt.domain.validation.ApplicationDraft
import io.github.aakocval.petadopt.domain.validation.ApplicationField
import io.github.aakocval.petadopt.domain.validation.FieldError
import kotlinx.collections.immutable.ImmutableMap
import kotlinx.collections.immutable.persistentMapOf

/** Shared by Form, Review and Success: one state for the whole flow. */
data class AdoptionUiState(
    val petName: String? = null,
    val draft: ApplicationDraft = ApplicationDraft(),
    /** Empty until the user first tries to continue; then kept live as they edit. */
    val errors: ImmutableMap<ApplicationField, FieldError> = persistentMapOf(),
    val isSubmitting: Boolean = false,
    val submitFailed: Boolean = false,
    /** Set once the shelter has the application. */
    val reference: String? = null,
)

/** Step changes are events: the ViewModel says *what* happened, the nav graph decides where to go. */
sealed interface AdoptionEvent {
    data object ShowReview : AdoptionEvent

    data object Submitted : AdoptionEvent
}
