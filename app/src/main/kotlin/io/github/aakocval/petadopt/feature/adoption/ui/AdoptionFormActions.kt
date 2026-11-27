package io.github.aakocval.petadopt.feature.adoption.ui

import io.github.aakocval.petadopt.domain.model.HomeType

/** All form callbacks in one place, so the screen signature stays readable. */
internal data class AdoptionFormActions(
    val onFullNameChange: (String) -> Unit = {},
    val onEmailChange: (String) -> Unit = {},
    val onPhoneChange: (String) -> Unit = {},
    val onHomeTypeSelect: (HomeType) -> Unit = {},
    val onHasOtherPetsChange: (Boolean) -> Unit = {},
    val onMessageChange: (String) -> Unit = {},
    val onHomeVisitChange: (Boolean) -> Unit = {},
    val onContinue: () -> Unit = {},
    val onBack: () -> Unit = {},
)
