package io.github.aakocval.petadopt.domain.validation

/** Password rules shared by the login form and the (fake) auth backend. */
object PasswordPolicy {
    const val MIN_LENGTH = 8

    /** Upper bound so a pasted megabyte can't reach hashing or the network. */
    const val MAX_LENGTH = 128

    fun isLongEnough(password: String): Boolean = password.length >= MIN_LENGTH
}
