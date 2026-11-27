package io.github.aakocval.petadopt.core.security

import android.os.Build
import androidx.activity.compose.LocalActivity
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricManager.Authenticators.BIOMETRIC_STRONG
import androidx.biometric.BiometricManager.Authenticators.BIOMETRIC_WEAK
import androidx.biometric.BiometricManager.Authenticators.DEVICE_CREDENTIAL
import androidx.biometric.BiometricPrompt
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity

/**
 * Re-authenticates the person holding the phone (biometric, or the device PIN/pattern/password)
 * before a sensitive action. It proves presence, not identity to our backend.
 */
class BiometricGate(
    private val activity: FragmentActivity,
) {
    // Android 11+ allows strong biometrics or the device credential together; older versions only
    // allow the credential alongside "weak" biometrics.
    private val authenticators =
        if (Build.VERSION.SDK_INT >=
            Build.VERSION_CODES.R
        ) {
            BIOMETRIC_STRONG or DEVICE_CREDENTIAL
        } else {
            BIOMETRIC_WEAK or DEVICE_CREDENTIAL
        }

    /** False when the device has no screen lock at all: there is nothing to verify against. */
    fun isAvailable(): Boolean =
        BiometricManager.from(activity).canAuthenticate(authenticators) == BiometricManager.BIOMETRIC_SUCCESS

    fun authenticate(
        title: String,
        onSuccess: () -> Unit,
    ) {
        val callback =
            object : BiometricPrompt.AuthenticationCallback() {
                override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) = onSuccess()
            }
        val prompt = BiometricPrompt(activity, ContextCompat.getMainExecutor(activity), callback)
        // No negative button: with DEVICE_CREDENTIAL allowed, the system provides "Use PIN" instead.
        prompt.authenticate(
            BiometricPrompt.PromptInfo
                .Builder()
                .setTitle(title)
                .setAllowedAuthenticators(authenticators)
                .build(),
        )
    }
}

/** The gate for the current activity, or `null` in previews and tests (no FragmentActivity). */
@Composable
fun rememberBiometricGate(): BiometricGate? {
    val activity = LocalActivity.current as? FragmentActivity ?: return null
    return remember(activity) { BiometricGate(activity) }
}
