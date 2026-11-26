package io.github.aakocval.petadopt.feature.auth.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.autofill.ContentType
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.contentType
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import io.github.aakocval.petadopt.R
import io.github.aakocval.petadopt.core.designsystem.icon.PetAdoptIcons
import io.github.aakocval.petadopt.core.designsystem.theme.PetAdoptTheme
import io.github.aakocval.petadopt.core.designsystem.theme.Spacing
import io.github.aakocval.petadopt.domain.validation.PasswordPolicy

@Composable
fun LoginScreen(
    uiState: LoginUiState,
    onEmailChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    onSubmit: () -> Unit,
    modifier: Modifier = Modifier,
) {
    // Purely visual, so it's local UI state rather than ViewModel state.
    var showPassword by rememberSaveable { mutableStateOf(false) }

    Column(
        modifier =
            modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .safeDrawingPadding()
                .imePadding()
                .verticalScroll(rememberScrollState())
                .padding(Spacing.xl),
        verticalArrangement = Arrangement.spacedBy(Spacing.lg),
    ) {
        Box(
            modifier =
                Modifier
                    .padding(top = Spacing.xxl)
                    .size(72.dp)
                    .background(PetAdoptTheme.colors.tileFor("auth"), CircleShape),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                painterResource(PetAdoptIcons.Paw),
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
            )
        }
        Column(verticalArrangement = Arrangement.spacedBy(Spacing.xs)) {
            Text(stringResource(R.string.auth_title), style = MaterialTheme.typography.headlineLarge)
            Text(
                text = stringResource(R.string.auth_subtitle),
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
        OutlinedTextField(
            value = uiState.email,
            onValueChange = onEmailChange,
            label = { Text(stringResource(R.string.auth_email)) },
            isError = uiState.emailError != null,
            supportingText = uiState.emailError?.let { error -> { Text(stringResource(error.message)) } },
            singleLine = true,
            keyboardOptions =
                KeyboardOptions(
                    keyboardType = KeyboardType.Email,
                    imeAction = ImeAction.Next,
                    autoCorrectEnabled = false,
                ),
            shape = MaterialTheme.shapes.medium,
            // Lets password managers offer saved accounts.
            modifier =
                Modifier.fillMaxWidth().semantics {
                    contentType = ContentType.Username + ContentType.EmailAddress
                },
        )
        OutlinedTextField(
            value = uiState.password,
            onValueChange = onPasswordChange,
            label = { Text(stringResource(R.string.auth_password)) },
            isError = uiState.passwordError != null,
            supportingText = uiState.passwordError?.let { error -> { Text(passwordErrorText(error)) } },
            singleLine = true,
            visualTransformation = if (showPassword) VisualTransformation.None else PasswordVisualTransformation(),
            trailingIcon = {
                TextButton(onClick = { showPassword = !showPassword }) {
                    Text(stringResource(if (showPassword) R.string.auth_hide_password else R.string.auth_show_password))
                }
            },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password, imeAction = ImeAction.Done),
            keyboardActions = KeyboardActions(onDone = { onSubmit() }),
            shape = MaterialTheme.shapes.medium,
            modifier = Modifier.fillMaxWidth().semantics { contentType = ContentType.Password },
        )
        if (uiState.signInFailed) {
            Text(
                text = stringResource(R.string.auth_error_rejected),
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodyMedium,
                // Announced by TalkBack as soon as it appears.
                modifier = Modifier.semantics { liveRegion = LiveRegionMode.Polite },
            )
        }
        Button(
            onClick = onSubmit,
            enabled = !uiState.isSubmitting,
            modifier = Modifier.fillMaxWidth().height(52.dp),
            shape = MaterialTheme.shapes.medium,
        ) {
            if (uiState.isSubmitting) {
                CircularProgressIndicator(modifier = Modifier.size(20.dp), strokeWidth = 2.dp)
            } else {
                Text(stringResource(R.string.auth_sign_in))
            }
        }
        Text(
            text = stringResource(R.string.auth_demo_hint),
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

private val EmailError.message: Int
    get() =
        when (this) {
            EmailError.Blank -> R.string.auth_error_email_blank
            EmailError.Invalid -> R.string.auth_error_email_invalid
        }

@Composable
private fun passwordErrorText(error: PasswordError): String =
    when (error) {
        PasswordError.Blank -> {
            stringResource(R.string.auth_error_password_blank)
        }

        PasswordError.TooShort -> {
            pluralStringResource(
                R.plurals.auth_error_password_short,
                PasswordPolicy.MIN_LENGTH,
                PasswordPolicy.MIN_LENGTH,
            )
        }
    }

@PreviewLightDark
@Composable
private fun LoginScreenPreview() {
    PetAdoptTheme {
        Surface {
            LoginScreen(
                uiState = LoginUiState(email = "alex@", emailError = EmailError.Invalid, signInFailed = false),
                onEmailChange = {},
                onPasswordChange = {},
                onSubmit = {},
            )
        }
    }
}
