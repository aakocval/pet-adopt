package io.github.aakocval.petadopt.feature.adoption.ui

import androidx.annotation.StringRes
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import io.github.aakocval.petadopt.R
import io.github.aakocval.petadopt.domain.model.HomeType
import io.github.aakocval.petadopt.domain.validation.AdoptionApplicationValidator
import io.github.aakocval.petadopt.domain.validation.ApplicationField
import io.github.aakocval.petadopt.domain.validation.FieldError

/** Turns a (field, error) pair into a message. The rules know *what* is wrong; this says it. */
@Composable
internal fun errorMessage(
    field: ApplicationField,
    error: FieldError,
): String =
    when (field) {
        ApplicationField.FullName -> nameError(error)
        ApplicationField.Email -> requiredOr(error, R.string.adoption_error_email)
        ApplicationField.Phone -> phoneError(error)
        ApplicationField.HomeType -> stringResource(R.string.adoption_error_home_type)
        ApplicationField.Message -> messageError(error)
        ApplicationField.HomeVisit -> stringResource(R.string.adoption_error_home_visit)
    }

@Composable
private fun requiredOr(
    error: FieldError,
    @StringRes otherwise: Int,
): String = stringResource(if (error == FieldError.Required) R.string.adoption_error_required else otherwise)

@Composable
private fun nameError(error: FieldError): String =
    when (error) {
        FieldError.InvalidCharacters -> stringResource(R.string.adoption_error_name_characters)
        else -> requiredOr(error, R.string.adoption_error_name_short)
    }

@Composable
private fun phoneError(error: FieldError): String =
    when (error) {
        FieldError.TooShort -> stringResource(R.string.adoption_error_phone_short)
        FieldError.TooLong -> stringResource(R.string.adoption_error_phone_long)
        else -> requiredOr(error, R.string.adoption_error_phone_format)
    }

@Composable
private fun messageError(error: FieldError): String =
    when (error) {
        FieldError.Required -> {
            stringResource(R.string.adoption_error_required)
        }

        else -> {
            AdoptionApplicationValidator.MESSAGE_MIN_LENGTH.let {
                pluralStringResource(R.plurals.adoption_error_message_short, it, it)
            }
        }
    }

@Composable
internal fun homeTypeLabel(type: HomeType): String =
    stringResource(
        when (type) {
            HomeType.House -> R.string.adoption_home_house
            HomeType.Apartment -> R.string.adoption_home_apartment
            HomeType.Other -> R.string.adoption_home_other
        },
    )

@Composable
internal fun adoptionTitle(petName: String?): String =
    petName?.let { stringResource(R.string.adoption_title, it) } ?: stringResource(R.string.adoption_title_generic)
