package io.github.aakocval.petadopt.core.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import io.github.aakocval.petadopt.R

private const val MONTHS_PER_YEAR = 12

/** "8 mos" under a year, "2 yrs" after. Pluralized per locale via resources. */
@Composable
fun petAgeLabel(ageInMonths: Int): String =
    if (ageInMonths < MONTHS_PER_YEAR) {
        pluralStringResource(R.plurals.pet_age_months, ageInMonths, ageInMonths)
    } else {
        val years = ageInMonths / MONTHS_PER_YEAR
        pluralStringResource(R.plurals.pet_age_years, years, years)
    }

/** "Beagle · 2 yrs", the secondary line on pet tiles. */
@Composable
fun petSubtitle(
    breed: String,
    ageInMonths: Int,
): String = stringResource(R.string.pet_subtitle, breed, petAgeLabel(ageInMonths))
