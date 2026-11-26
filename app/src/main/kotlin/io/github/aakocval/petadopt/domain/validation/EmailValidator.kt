package io.github.aakocval.petadopt.domain.validation

/**
 * Pragmatic email check: one `@`, a non-empty local part, and a dotted domain, no spaces.
 * Deliberately not RFC 5322: the server is the real authority; this only catches typos early.
 * Pure Kotlin (no `android.util.Patterns`), so the domain layer stays Android-free and testable.
 */
object EmailValidator {
    /** RFC 5321 limit for a full address. */
    const val MAX_LENGTH = 254

    private val pattern = Regex("^[^@\\s]+@[^@\\s]+\\.[^@\\s]{2,}$")

    fun isValid(email: String): Boolean = email.length <= MAX_LENGTH && pattern.matches(email)
}
