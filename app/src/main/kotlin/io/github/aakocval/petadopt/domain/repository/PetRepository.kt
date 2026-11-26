package io.github.aakocval.petadopt.domain.repository

import io.github.aakocval.petadopt.domain.model.Pet
import kotlinx.coroutines.flow.Flow

/**
 * Source of truth for pets. Callers observe a [Flow] and never ask "is it loaded yet?":
 * the implementation decides where data comes from (memory now, cache + network in M6).
 */
interface PetRepository {
    /** All adoptable pets. Emits again whenever the underlying data changes. */
    fun observePets(): Flow<List<Pet>>

    /** One pet, or `null` if no pet has this id (e.g. a stale deep link). */
    fun observePet(id: String): Flow<Pet?>
}
