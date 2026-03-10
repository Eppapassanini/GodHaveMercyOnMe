package com.example.tvmazeshows.ui.list

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tvmazeshows.domain.model.Show
import com.example.tvmazeshows.domain.repository.ShowRepository
import com.example.tvmazeshows.ui.state.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ShowsListViewModel @Inject constructor(
    private val repository: ShowRepository
) : ViewModel() {

    private val _uiState = mutableStateOf<UiState<List<Show>>>(UiState.Loading)
    val uiState: State<UiState<List<Show>>> = _uiState


    init {
        loadShows()
    }

    fun loadShows() {
        viewModelScope.launch {
            _uiState.value = UiState.Loading
            repository.getShows(page = 0)
                .onSuccess { shows ->
                    _uiState.value = UiState.Content(shows.take(100))
                }
                .onFailure { error ->
                    _uiState.value = UiState.Error(
                        message = "Failed to load shows: ${error.localizedMessage ?: "Unknown error"}",
                        retryAction = { loadShows() }
                    )
                }
        }
    }
}
