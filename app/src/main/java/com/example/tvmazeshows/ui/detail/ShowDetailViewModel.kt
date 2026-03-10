package com.example.tvmazeshows.ui.detail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tvmazeshows.domain.model.Show
import com.example.tvmazeshows.domain.repository.ShowRepository
import com.example.tvmazeshows.ui.state.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ShowDetailViewModel @Inject constructor(
    private val repository: ShowRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val showId: Int = checkNotNull(savedStateHandle["showId"])


    private val _uiState = MutableStateFlow<UiState<Show>>(UiState.Loading)
    val uiState = _uiState.asStateFlow()


    private val _selectedTab = MutableStateFlow(0)
    val selectedTab = _selectedTab.asStateFlow()

    val tabs = listOf("Info", "Related", "Links")


    private val _relatedShowsState = MutableStateFlow<UiState<List<Show>>>(UiState.Loading)
    val relatedShowsState = _relatedShowsState.asStateFlow()

    init {
        loadShowDetails()
    }

    fun loadRelatedShows(currentShow: Show) {
        viewModelScope.launch {
            _relatedShowsState.value = UiState.Loading

            repository.getShows(page = 0)
                .onSuccess { allShows ->
                    val similar = allShows
                        .filter { it.id != currentShow.id }
                        .filter { show ->
                            show.genres.any { it in currentShow.genres }
                        }
                        .take(5)

                    if (similar.isNotEmpty()) {
                        _relatedShowsState.value = UiState.Content(similar)
                    } else {
                        _relatedShowsState.value = UiState.Content(emptyList())
                    }
                }
                .onFailure { error ->
                    _relatedShowsState.value = UiState.Error(
                        message = "Failed to load related shows: ${error.localizedMessage ?: "Unknown error"}",
                        retryAction = { loadRelatedShows(currentShow) }
                    )
                }
        }
    }

    fun loadShowDetails() {
        viewModelScope.launch {
            _uiState.value = UiState.Loading
            repository.getShowDetails(showId)
                .onSuccess { show ->
                    _uiState.value = UiState.Content(show)
                }
                .onFailure { error ->
                    _uiState.value = UiState.Error(
                        message = "Failed to load: ${error.localizedMessage ?: "Unknown error"}",
                        retryAction = { loadShowDetails() }
                    )
                }
        }
    }

    fun selectTab(index: Int) {
        _selectedTab.value = index
    }
}
