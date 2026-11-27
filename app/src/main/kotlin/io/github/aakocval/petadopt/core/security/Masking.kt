package io.github.aakocval.petadopt.core.security

private const val MAX_MASK = 8

/** "alex.morgan@example.com" -> "a••••••••@example.com". Keeps the domain for recognisability. */
fun maskEmail(email: String): String {
    val at = email.indexOf('@')
    if (at <= 0) return "•".repeat(minOf(email.length, MAX_MASK))
    return email.first() + "•".repeat(minOf(at - 1, MAX_MASK).coerceAtLeast(1)) + email.substring(at)
}
