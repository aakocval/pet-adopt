package io.github.aakocval.petadopt.feature.adoption.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.autofill.ContentType
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentType
import androidx.compose.ui.semantics.error
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import io.github.aakocval.petadopt.R
import io.github.aakocval.petadopt.core.designsystem.theme.PetAdoptTheme
import io.github.aakocval.petadopt.core.designsystem.theme.Spacing
import io.github.aakocval.petadopt.domain.model.HomeType
import io.github.aakocval.petadopt.domain.validation.AdoptionApplicationValidator
import io.github.aakocval.petadopt.domain.validation.ApplicationDraft
import io.github.aakocval.petadopt.domain.validation.ApplicationField
import io.github.aakocval.petadopt.domain.validation.FieldError
import kotlinx.collections.immutable.persistentMapOf

@Composable
internal fun AdoptionFormScreen(
    uiState: AdoptionUiState,
    actions: AdoptionFormActions,
    modifier: Modifier = Modifier,
) {
    val draft = uiState.draft
    val errors = uiState.errors
    Column(modifier = modifier.fillMaxSize().imePadding()) {
        AdoptionTopBar(title = adoptionTitle(uiState.petName), step = 1, onBack = actions.onBack)
        Column(
            modifier =
                Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = Spacing.screenHorizontal, vertical = Spacing.sm),
            verticalArrangement = Arrangement.spacedBy(Spacing.md),
        ) {
            FormField(
                value = draft.fullName,
                onValueChange = actions.onFullNameChange,
                label = stringResource(R.string.adoption_full_name),
                field = ApplicationField.FullName,
                error = errors[ApplicationField.FullName],
                keyboardOptions =
                    KeyboardOptions(
                        capitalization = KeyboardCapitalization.Words,
                        imeAction = ImeAction.Next,
                    ),
                autofill = ContentType.PersonFullName,
            )
            FormField(
                value = draft.email,
                onValueChange = actions.onEmailChange,
                label = stringResource(R.string.adoption_email),
                field = ApplicationField.Email,
                error = errors[ApplicationField.Email],
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email, imeAction = ImeAction.Next),
                autofill = ContentType.EmailAddress,
            )
            FormField(
                value = draft.phone,
                onValueChange = actions.onPhoneChange,
                label = stringResource(R.string.adoption_phone),
                field = ApplicationField.Phone,
                error = errors[ApplicationField.Phone],
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone, imeAction = ImeAction.Next),
                autofill = ContentType.PhoneNumber,
            )
            HomeTypePicker(
                selected = draft.homeType,
                error = errors[ApplicationField.HomeType],
                onSelect = actions.onHomeTypeSelect,
            )
            Row(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .toggleable(
                            value = draft.hasOtherPets,
                            role = Role.Switch,
                            onValueChange = actions.onHasOtherPetsChange,
                        ).padding(vertical = Spacing.xs),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(stringResource(R.string.adoption_other_pets), modifier = Modifier.weight(1f))
                Switch(checked = draft.hasOtherPets, onCheckedChange = null)
            }
            FormField(
                value = draft.message,
                onValueChange = actions.onMessageChange,
                label = stringResource(R.string.adoption_message),
                field = ApplicationField.Message,
                error = errors[ApplicationField.Message],
                keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Sentences),
                singleLine = false,
                counter =
                    stringResource(
                        R.string.adoption_message_counter,
                        draft.message.length,
                        AdoptionApplicationValidator.MESSAGE_MAX_LENGTH,
                    ),
            )
            HomeVisitConsent(
                checked = draft.agreesToHomeVisit,
                error = errors[ApplicationField.HomeVisit],
                onCheckedChange = actions.onHomeVisitChange,
            )
        }
        Button(
            onClick = actions.onContinue,
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(Spacing.screenHorizontal)
                    .height(52.dp),
            shape = MaterialTheme.shapes.medium,
        ) { Text(stringResource(R.string.adoption_continue)) }
    }
}

@Composable
private fun FormField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    field: ApplicationField,
    error: FieldError?,
    keyboardOptions: KeyboardOptions,
    autofill: ContentType? = null,
    singleLine: Boolean = true,
    counter: String? = null,
) {
    val errorText = error?.let { errorMessage(field, it) }
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        isError = error != null,
        supportingText =
            if (errorText != null || counter != null) {
                {
                    Row {
                        Text(errorText.orEmpty(), modifier = Modifier.weight(1f))
                        counter?.let { Text(it) }
                    }
                }
            } else {
                null
            },
        singleLine = singleLine,
        minLines = if (singleLine) 1 else MESSAGE_MIN_LINES,
        keyboardOptions = keyboardOptions,
        shape = MaterialTheme.shapes.medium,
        modifier =
            Modifier
                .fillMaxWidth()
                .semantics {
                    autofill?.let { contentType = it }
                    errorText?.let { error(it) }
                },
    )
}

@Composable
private fun HomeTypePicker(
    selected: HomeType?,
    error: FieldError?,
    onSelect: (HomeType) -> Unit,
) {
    Column(verticalArrangement = Arrangement.spacedBy(Spacing.xs)) {
        Text(stringResource(R.string.adoption_home_type), style = MaterialTheme.typography.titleSmall)
        Row(horizontalArrangement = Arrangement.spacedBy(Spacing.sm)) {
            HomeType.entries.forEach { type ->
                FilterChip(
                    selected = type == selected,
                    onClick = { onSelect(type) },
                    label = { Text(homeTypeLabel(type)) },
                    shape = MaterialTheme.shapes.extraLarge,
                )
            }
        }
        error?.let { ErrorText(errorMessage(ApplicationField.HomeType, it)) }
    }
}

@Composable
private fun HomeVisitConsent(
    checked: Boolean,
    error: FieldError?,
    onCheckedChange: (Boolean) -> Unit,
) {
    Column {
        Row(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .toggleable(value = checked, role = Role.Checkbox, onValueChange = onCheckedChange),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Checkbox(checked = checked, onCheckedChange = null)
            Text(stringResource(R.string.adoption_home_visit), modifier = Modifier.padding(start = Spacing.sm))
        }
        error?.let { ErrorText(errorMessage(ApplicationField.HomeVisit, it)) }
    }
}

@Composable
private fun ErrorText(text: String) {
    Text(text = text, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
}

private const val MESSAGE_MIN_LINES = 4

@PreviewLightDark
@Composable
private fun AdoptionFormScreenPreview() {
    PetAdoptTheme {
        Surface(color = MaterialTheme.colorScheme.background) {
            AdoptionFormScreen(
                uiState =
                    AdoptionUiState(
                        petName = "Biscuit",
                        draft = ApplicationDraft(fullName = "Alex", email = "alex@example.com", phone = "555"),
                        errors =
                            persistentMapOf(
                                ApplicationField.Phone to FieldError.TooShort,
                                ApplicationField.HomeType to FieldError.Required,
                            ),
                    ),
                actions = AdoptionFormActions(),
            )
        }
    }
}
