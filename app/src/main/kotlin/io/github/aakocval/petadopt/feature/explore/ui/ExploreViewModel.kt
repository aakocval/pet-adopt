package io.github.aakocval.petadopt.feature.explore.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.aakocval.petadopt.domain.model.Pet
import io.github.aakocval.petadopt.domain.repository.PetRepository
import io.github.aakocval.petadopt.feature.explore.map.MapPin
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

/** Pets as map pins, plus which one is selected. Map rendering is the MapProvider's job, not this. */
@HiltViewModel
class ExploreViewModel
    @Inject
    constructor(
        petRepository: PetRepository,
    ) : ViewModel() {
        private val selectedId = MutableStateFlow<String?>(null)

        val uiState: StateFlow<ExploreUiState> =
            combine(petRepository.observePets(), selectedId) { pets, selectedId ->
                ExploreUiState(
                    isLoading = false,
                    pins = pets.map { MapPin(id = it.id, location = it.location, label = it.name) }.toImmutableList(),
                    selected = pets.firstOrNull { it.id == selectedId }?.toCard(),
                )
            }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(STOP_TIMEOUT_MILLIS), ExploreUiState())

        /** `null` (a tap on empty map) clears the selection. */
        fun onPinSelected(petId: String?) {
            selectedId.value = petId
        }

        private fun Pet.toCard() =
            ExplorePetCard(
                id = id,
                name = name,
                breed = breed,
                ageInMonths = ageInMonths,
                species = species,
                shelterName = shelterName,
                photoUrl = photoUrl,
            )

        private companion object {
            const val STOP_TIMEOUT_MILLIS = 5_000L
        }
    }
