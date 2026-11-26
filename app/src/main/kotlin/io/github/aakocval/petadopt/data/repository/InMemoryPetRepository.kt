package io.github.aakocval.petadopt.data.repository

import io.github.aakocval.petadopt.data.local.MockPets
import io.github.aakocval.petadopt.domain.model.Pet
import io.github.aakocval.petadopt.domain.repository.PetRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Pets held in memory. The reference implementation for the vertical slice; replaced by an
 * offline-first repository (local cache + remote photos) in M6 without touching any caller.
 */
@Singleton
class InMemoryPetRepository internal constructor(
    seed: List<Pet>,
) : PetRepository {
    @Inject
    constructor() : this(MockPets.all)

    private val pets = MutableStateFlow(seed)

    override fun observePets(): Flow<List<Pet>> = pets.asStateFlow()

    override fun observePet(id: String): Flow<Pet?> =
        pets
            .map { all ->
                all.firstOrNull {
                    it.id == id
                }
            }.distinctUntilChanged()
}
