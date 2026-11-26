package io.github.aakocval.petadopt.testing

import io.github.aakocval.petadopt.domain.model.Pet
import io.github.aakocval.petadopt.domain.repository.PetRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import java.io.IOException

/**
 * Hand-written fake: nothing is emitted until a test calls [send], so "loading" is observable.
 * Preferred over mocks for stateful dependencies, since tests read like real usage.
 */
class FakePetRepository : PetRepository {
    private val pets = MutableSharedFlow<List<Pet>>(replay = 1)
    var failNextSubscription = false

    suspend fun send(value: List<Pet>) = pets.emit(value)

    override fun observePets(): Flow<List<Pet>> =
        flow {
            if (failNextSubscription) {
                failNextSubscription = false
                throw IOException("Simulated failure")
            }
            pets.collect { emit(it) }
        }

    override fun observePet(id: String): Flow<Pet?> = pets.map { all -> all.firstOrNull { it.id == id } }
}
