package io.github.aakocval.petadopt.data.repository

import io.github.aakocval.petadopt.core.common.AppDispatcher
import io.github.aakocval.petadopt.core.common.ApplicationScope
import io.github.aakocval.petadopt.core.common.Dispatcher
import io.github.aakocval.petadopt.data.local.PetCatalogSource
import io.github.aakocval.petadopt.data.local.PhotoUrlCache
import io.github.aakocval.petadopt.data.model.PetRecord
import io.github.aakocval.petadopt.data.model.SpeciesRecord
import io.github.aakocval.petadopt.data.model.toDomain
import io.github.aakocval.petadopt.data.remote.RemotePhotoSource
import io.github.aakocval.petadopt.domain.model.Pet
import io.github.aakocval.petadopt.domain.repository.PetRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import kotlinx.serialization.SerializationException
import retrofit2.HttpException
import java.io.IOException
import java.util.concurrent.atomic.AtomicBoolean
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Offline-first pets:
 *
 * 1. On first subscription, emit the bundled catalog overlaid with cached photo URLs. That's
 *    local, so the list shows instantly, with or without a network.
 * 2. Then, in the app scope, fetch photos still missing (dogs in parallel), emit again and
 *    persist the URLs. Failures leave a photo `null`; the UI shows an illustrated face, and the
 *    next subscriber retries.
 */
@Singleton
class OfflineFirstPetRepository
    @Inject
    constructor(
        private val catalog: PetCatalogSource,
        private val photoCache: PhotoUrlCache,
        private val photos: RemotePhotoSource,
        @param:Dispatcher(AppDispatcher.IO) private val ioDispatcher: CoroutineDispatcher,
        @param:ApplicationScope private val appScope: CoroutineScope,
    ) : PetRepository {
        private val records = MutableStateFlow<List<PetRecord>?>(null)
        private val startLock = Mutex()
        private var started = false
        private val refreshing = AtomicBoolean(false)

        override fun observePets(): Flow<List<Pet>> =
            records
                .onStart {
                    startOnce()
                    retryMissingPhotos()
                }.filterNotNull()
                .map { list -> list.map(PetRecord::toDomain) }
                .distinctUntilChanged()

        override fun observePet(id: String): Flow<Pet?> =
            observePets().map { pets -> pets.firstOrNull { it.id == id } }.distinctUntilChanged()

        private suspend fun startOnce() {
            val isFirst = startLock.withLock { (!started).also { started = true } }
            if (!isFirst) return
            val cached = photoCache.read()
            records.value = withContext(ioDispatcher) { catalog.load() }.map { it.copy(photoUrl = cached[it.id]) }
        }

        /**
         * Every new subscriber (e.g. returning to Browse) retries photos that are still missing, so a
         * first launch offline heals itself once the network is back. At most one refresh runs at a time.
         */
        private fun retryMissingPhotos() {
            val missing = records.value?.any { it.photoUrl == null } ?: return
            if (!missing || !refreshing.compareAndSet(false, true)) return
            appScope.launch {
                try {
                    refreshMissingPhotos()
                } finally {
                    refreshing.set(false)
                }
            }
        }

        /** Fetches photos for pets that don't have one yet. Safe to call again later (e.g. when back online). */
        suspend fun refreshMissingPhotos() {
            val current = records.value ?: return
            if (current.all { it.photoUrl != null }) return

            val updated =
                coroutineScope {
                    val missingCats = current.filter { it.species == SpeciesRecord.Cat && it.photoUrl == null }
                    val catUrls =
                        async {
                            if (missingCats.isEmpty()) {
                                emptyList()
                            } else {
                                orNull {
                                    photos.catPhotos(
                                        missingCats.size,
                                    )
                                }.orEmpty()
                            }
                        }
                    val dogUrls =
                        current
                            .filter { it.species == SpeciesRecord.Dog && it.photoUrl == null && it.photoQuery != null }
                            .map { dog ->
                                async { dog.id to orNull { photos.dogPhoto(requireNotNull(dog.photoQuery)) } }
                            }.awaitAll()
                            .toMap()
                    val catUrlFor = missingCats.map { it.id }.zip(catUrls.await()).toMap()
                    current.map { it.copy(photoUrl = it.photoUrl ?: dogUrls[it.id] ?: catUrlFor[it.id]) }
                }

            if (updated != current) {
                records.value = updated
                photoCache.write(updated.mapNotNull { pet -> pet.photoUrl?.let { pet.id to it } }.toMap())
            }
        }

        /** Network, HTTP and malformed-response errors degrade to "no photo"; anything else is a bug and propagates. */
        private suspend fun <T> orNull(block: suspend () -> T): T? =
            try {
                block()
            } catch (_: IOException) {
                null
            } catch (_: HttpException) {
                null
            } catch (_: SerializationException) {
                null
            }
    }
