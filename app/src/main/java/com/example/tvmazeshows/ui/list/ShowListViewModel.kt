package com.example.tvmazeshows.ui.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tvmazeshows.domain.repository.ShowRepository
import com.example.tvmazeshows.ui.state.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ShowsListViewModel @Inject constructor(
    private val repository: ShowRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<UiState<List<com.example.tvmazeshows.domain.model.Show>>>(UiState.Loading)
    val uiState = _uiState.asStateFlow()

    private val _navigation = MutableSharedFlow<Int>()
    val navigation = _navigation.asSharedFlow()

    init {
        loadShows()
    }

    fun loadShows() {
        viewModelScope.launch {
            _uiState.value = UiState.Loading
            repository.getShows(page = 0)
                .onSuccess { shows ->
                    _uiState.value = UiState.Content(shows.take(20))
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