package io.github.aakocval.petadopt.core.designsystem.icon

import androidx.annotation.DrawableRes
import io.github.aakocval.petadopt.R

/**
 * Every icon the app uses, in one place. Vector drawables instead of material-icons-extended
 * keep the APK small: only icons we ship end up in it.
 */
object PetAdoptIcons {
    @DrawableRes val Paw = R.drawable.ic_paw

    @DrawableRes val Heart = R.drawable.ic_heart

    @DrawableRes val HeartOutline = R.drawable.ic_heart_outline

    @DrawableRes val Person = R.drawable.ic_person

    @DrawableRes val MapPin = R.drawable.ic_map_pin

    @DrawableRes val ChevronRight = R.drawable.ic_chevron_right
}
