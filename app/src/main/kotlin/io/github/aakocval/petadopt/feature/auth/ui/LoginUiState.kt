package io.github.aakocval.petadopt.feature.auth.ui

/**
 * Login form state. Errors are enums, not strings: the ViewModel decides *what* is wrong and the
 * screen decides how to say it (localized, accessible).
 */
data class LoginUiState(
    val email: String = "",
    val password: String = "",
    val emailError: EmailError? = null,
    val passwordError: PasswordError? = null,
    val isSubmitting: Boolean = false,
    val signInFailed: Boolean = false,
)

enum class EmailError { Blank, Invalid }

enum class PasswordError { Blank, TooShort }
