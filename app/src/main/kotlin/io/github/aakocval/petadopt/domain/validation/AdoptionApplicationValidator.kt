package io.github.aakocval.petadopt.domain.validation

import io.github.aakocval.petadopt.domain.model.AdoptionApplication
import io.github.aakocval.petadopt.domain.model.HomeType

/** What the user has typed so far. Anything goes here; [AdoptionApplicationValidator] decides what's acceptable. */
data class ApplicationDraft(
    val fullName: String = "",
    val email: String = "",
    val phone: String = "",
    val homeType: HomeType? = null,
    val hasOtherPets: Boolean = false,
    val message: String = "",
    val agreesToHomeVisit: Boolean = false,
)

enum class ApplicationField { FullName, Email, Phone, HomeType, Message, HomeVisit }

enum class FieldError { Required, TooShort, TooLong, InvalidCharacters, InvalidFormat }

/**
 * Every rule for an adoption application, in pure Kotlin.
 *
 * Runs on the normalized (trimmed, collapsed) draft when the user continues to review, and again
 * right before submitting. Keystroke-level cleanup is [ApplicationInputSanitizer]'s job.
 */
object AdoptionApplicationValidator {
    const val NAME_MAX_LENGTH = 80
    const val NAME_MIN_LETTERS = 2
    const val PHONE_MIN_DIGITS = 7
    const val PHONE_MAX_DIGITS = 15 // E.164
    const val MESSAGE_MIN_LENGTH = 20
    const val MESSAGE_MAX_LENGTH = 500

    private val whitespaceRun = Regex("\\s+")
    private val allowedNamePunctuation = setOf(' ', '-', '\'', '.')

    /** Final cleanup before validating or submitting. */
    fun normalize(draft: ApplicationDraft): ApplicationDraft =
        draft.copy(
            fullName = draft.fullName.trim().replace(whitespaceRun, " "),
            email = draft.email.trim(),
            phone = draft.phone.trim(),
            message = draft.message.trim(),
        )

    /** Errors per field for the normalized draft; empty means valid. */
    fun validate(draft: ApplicationDraft): Map<ApplicationField, FieldError> {
        val d = normalize(draft)
        return buildMap {
            nameError(d.fullName)?.let { put(ApplicationField.FullName, it) }
            emailError(d.email)?.let { put(ApplicationField.Email, it) }
            phoneError(d.phone)?.let { put(ApplicationField.Phone, it) }
            if (d.homeType == null) put(ApplicationField.HomeType, FieldError.Required)
            messageError(d.message)?.let { put(ApplicationField.Message, it) }
            if (!d.agreesToHomeVisit) put(ApplicationField.HomeVisit, FieldError.Required)
        }
    }

    /** The only way to build an [AdoptionApplication]: `null` unless the draft is valid. */
    fun toApplication(
        petId: String,
        draft: ApplicationDraft,
    ): AdoptionApplication? {
        if (validate(draft).isNotEmpty()) return null
        val d = normalize(draft)
        return AdoptionApplication(
            petId = petId,
            fullName = d.fullName,
            email = d.email,
            phone = normalizedPhone(d.phone),
            homeType = requireNotNull(d.homeType),
            hasOtherPets = d.hasOtherPets,
            message = d.message,
        )
    }

    private fun nameError(name: String): FieldError? =
        when {
            name.isEmpty() -> FieldError.Required
            name.any { !it.isLetter() && it !in allowedNamePunctuation } -> FieldError.InvalidCharacters
            name.count(Char::isLetter) < NAME_MIN_LETTERS -> FieldError.TooShort
            else -> null
        }

    private fun emailError(email: String): FieldError? =
        when {
            email.isEmpty() -> FieldError.Required
            !EmailValidator.isValid(email) -> FieldError.InvalidFormat
            else -> null
        }

    private fun phoneError(phone: String): FieldError? {
        val digits = phone.count(Char::isDigit)
        return when {
            phone.isEmpty() -> FieldError.Required

            phone.lastIndexOf('+') > 0 -> FieldError.InvalidFormat

            // '+' only as a prefix
            digits < PHONE_MIN_DIGITS -> FieldError.TooShort

            digits > PHONE_MAX_DIGITS -> FieldError.TooLong

            else -> null
        }
    }

    private fun messageError(message: String): FieldError? =
        when {
            message.isEmpty() -> FieldError.Required
            message.length < MESSAGE_MIN_LENGTH -> FieldError.TooShort
            else -> null
        }

    /** "+1 (555) 010-2030" -> "+15550102030". */
    private fun normalizedPhone(phone: String): String =
        (if (phone.startsWith('+')) "+" else "") + phone.filter(Char::isDigit)
}
