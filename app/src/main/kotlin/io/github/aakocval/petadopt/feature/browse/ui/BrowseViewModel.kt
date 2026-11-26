package io.github.aakocval.petadopt.feature.browse.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.aakocval.petadopt.domain.repository.FavoritesRepository
import io.github.aakocval.petadopt.domain.repository.PetRepository
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Browse screen logic. Unidirectional data flow:
 *
 * ```
 * user action ──> function on this ViewModel ──> repository / local state
 *       ▲                                               │
 *       └──────── uiState (StateFlow) <── combine ──────┘
 * ```
 *
 * Knows nothing about Compose, Context or navigation: taps that navigate are handled by the route.
 */
@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class BrowseViewModel
    @Inject
    constructor(
        private val petRepository: PetRepository,
        private val favoritesRepository: FavoritesRepository,
    ) : ViewModel() {
        private val filter = MutableStateFlow(SpeciesFilter.All)
        private val retries = MutableStateFlow(0)

        // BUFFERED channel: events sent while the UI is stopped wait, and each is delivered once.
        private val eventChannel = Channel<BrowseEvent>(Channel.BUFFERED)
        val events: Flow<BrowseEvent> = eventChannel.receiveAsFlow()

        val uiState: StateFlow<BrowseUiState> =
            retries
                .flatMapLatest {
                    combine(
                        petRepository.observePets(),
                        favoritesRepository.observeFavoriteIds(),
                        filter,
                    ) { pets, favoriteIds, filter ->
                        BrowseUiState(
                            filter = filter,
                            content =
                                BrowseContent.Pets(
                                    pets
                                        .filter(
                                            filter::matches,
                                        ).map { it.toCardUi(isFavorite = it.id in favoriteIds) }
                                        .toImmutableList(),
                                ),
                        )
                    }.onStart { emit(BrowseUiState(filter = filter.value, content = BrowseContent.Loading)) }
                        .catch { emit(BrowseUiState(filter = filter.value, content = BrowseContent.Error)) }
                }
                // Keep the upstream alive 5s after the last collector leaves: survives rotation without
                // restarting, but stops work when the app is in the background.
                .stateIn(viewModelScope, SharingStarted.WhileSubscribed(STOP_TIMEOUT_MILLIS), BrowseUiState())

        fun onFilterSelected(selected: SpeciesFilter) {
            filter.value = selected
        }

        fun onToggleFavorite(
            petId: String,
            isFavorite: Boolean,
        ) {
            val petName =
                (uiState.value.content as? BrowseContent.Pets)?.pets?.firstOrNull { it.id == petId }?.name ?: return
            viewModelScope.launch {
                favoritesRepository.setFavorite(petId, isFavorite)
                eventChannel.send(BrowseEvent.FavoriteChanged(petName, isFavorite))
            }
        }

        fun onRetry() {
            retries.value++
        }

        private companion object {
            const val STOP_TIMEOUT_MILLIS = 5_000L
        }
    }
