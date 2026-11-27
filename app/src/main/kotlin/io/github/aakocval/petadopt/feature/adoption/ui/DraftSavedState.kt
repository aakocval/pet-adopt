package io.github.aakocval.petadopt.feature.adoption.ui

import androidx.lifecycle.SavedStateHandle
import io.github.aakocval.petadopt.domain.model.HomeType
import io.github.aakocval.petadopt.domain.validation.ApplicationDraft

/*
 * Saves the in-progress draft into SavedStateHandle as plain primitives, so a half-filled form
 * survives the OS killing the app in the background. The domain draft stays free of Android
 * types (no Parcelable); the mapping lives here, at the edge.
 */

private const val KEY_NAME = "draft_full_name"
private const val KEY_EMAIL = "draft_email"
private const val KEY_PHONE = "draft_phone"
private const val KEY_HOME = "draft_home_type"
private const val KEY_OTHER_PETS = "draft_other_pets"
private const val KEY_MESSAGE = "draft_message"
private const val KEY_HOME_VISIT = "draft_home_visit"
internal const val KEY_REFERENCE = "submitted_reference"

internal fun SavedStateHandle.restoreDraft() =
    ApplicationDraft(
        fullName = get<String>(KEY_NAME).orEmpty(),
        email = get<String>(KEY_EMAIL).orEmpty(),
        phone = get<String>(KEY_PHONE).orEmpty(),
        homeType = get<String>(KEY_HOME)?.let { name -> HomeType.entries.firstOrNull { it.name == name } },
        hasOtherPets = get<Boolean>(KEY_OTHER_PETS) ?: false,
        message = get<String>(KEY_MESSAGE).orEmpty(),
        agreesToHomeVisit = get<Boolean>(KEY_HOME_VISIT) ?: false,
    )

internal fun SavedStateHandle.saveDraft(draft: ApplicationDraft) {
    set(KEY_NAME, draft.fullName)
    set(KEY_EMAIL, draft.email)
    set(KEY_PHONE, draft.phone)
    set(KEY_HOME, draft.homeType?.name)
    set(KEY_OTHER_PETS, draft.hasOtherPets)
    set(KEY_MESSAGE, draft.message)
    set(KEY_HOME_VISIT, draft.agreesToHomeVisit)
}
