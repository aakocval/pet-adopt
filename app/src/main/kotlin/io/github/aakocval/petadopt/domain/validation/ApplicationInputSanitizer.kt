package io.github.aakocval.petadopt.domain.validation

/**
 * Keystroke-level cleanup for the adoption form. It only removes what can never be valid
 * (control characters, impossible symbols, input past the length cap) and never trims, so typing
 * a space between first and last name still works. Real validation happens in
 * [AdoptionApplicationValidator].
 */
object ApplicationInputSanitizer {
    const val PHONE_MAX_LENGTH = 20

    private val allowedPhoneSymbols = setOf('+', '-', '(', ')', ' ')

    fun name(raw: String): String = raw.filterNot(Char::isISOControl).take(AdoptionApplicationValidator.NAME_MAX_LENGTH)

    fun email(raw: String): String = raw.filterNot(Char::isWhitespace).take(EmailValidator.MAX_LENGTH)

    fun phone(raw: String): String = raw.filter { it.isDigit() || it in allowedPhoneSymbols }.take(PHONE_MAX_LENGTH)

    /** Keeps line breaks, drops other control characters (e.g. pasted NULs or escape codes). */
    fun message(raw: String): String =
        raw.filter { !it.isISOControl() || it == '\n' }.take(AdoptionApplicationValidator.MESSAGE_MAX_LENGTH)
}
