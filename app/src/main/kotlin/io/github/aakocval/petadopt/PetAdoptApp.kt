package io.github.aakocval.petadopt

import android.app.Application
import coil3.ImageLoader
import coil3.PlatformContext
import coil3.SingletonImageLoader
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

/**
 * Application entry point. Deliberately does no work in onCreate: the image loader is only
 * built when the first image is requested (dagger.Lazy), and DataStore opens on first read.
 */
@HiltAndroidApp
class PetAdoptApp :
    Application(),
    SingletonImageLoader.Factory {
    @Inject
    lateinit var imageLoader: dagger.Lazy<ImageLoader>

    override fun newImageLoader(context: PlatformContext): ImageLoader = imageLoader.get()
}
