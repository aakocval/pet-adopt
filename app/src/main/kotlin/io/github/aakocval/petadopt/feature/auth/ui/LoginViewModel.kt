package io.github.aakocval.petadopt.feature.auth.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.aakocval.petadopt.domain.repository.AuthRepository
import io.github.aakocval.petadopt.domain.repository.SignInResult
import io.github.aakocval.petadopt.domain.validation.EmailValidator
import io.github.aakocval.petadopt.domain.validation.PasswordPolicy
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Login form logic. On success it does nothing else: the app root observes the signed-in user
 * and replaces the auth graph with the main graph, so Back can't return to this screen.
 *
 * The password lives only in this in-memory state; it's never logged, persisted or put in a
 * SavedStateHandle.
 */
@HiltViewModel
class LoginViewModel
    @Inject
    constructor(
        private val authRepository: AuthRepository,
    ) : ViewModel() {
        private val state = MutableStateFlow(LoginUiState())
        val uiState: StateFlow<LoginUiState> = state.asStateFlow()

        fun onEmailChange(value: String) {
            // Sanitize as the user types: no whitespace in an address, and a hard length cap.
            val email = value.filterNot(Char::isWhitespace).take(EmailValidator.MAX_LENGTH)
            state.update { it.copy(email = email, emailError = null, signInFailed = false) }
        }

        fun onPasswordChange(value: String) {
            state.update {
                it.copy(
                    password = value.take(PasswordPolicy.MAX_LENGTH),
                    passwordError = null,
                    signInFailed = false,
                )
            }
        }

        fun onSubmit() {
            val current = state.value
            if (current.isSubmitting) return // double taps don't send two requests

            val emailError =
                when {
                    current.email.isBlank() -> EmailError.Blank
                    !EmailValidator.isValid(current.email) -> EmailError.Invalid
                    else -> null
                }
            val passwordError =
                when {
                    current.password.isEmpty() -> PasswordError.Blank
                    !PasswordPolicy.isLongEnough(current.password) -> PasswordError.TooShort
                    else -> null
                }
            if (emailError != null || passwordError != null) {
                state.update { it.copy(emailError = emailError, passwordError = passwordError) }
                return
            }

            state.update { it.copy(isSubmitting = true, signInFailed = false) }
            viewModelScope.launch {
                val result = authRepository.signIn(current.email, current.password)
                state.update {
                    it.copy(
                        isSubmitting = false,
                        signInFailed = result is SignInResult.InvalidCredentials,
                        // Don't keep the password around after an attempt.
                        password = if (result is SignInResult.Success) "" else it.password,
                    )
                }
            }
        }
    }
