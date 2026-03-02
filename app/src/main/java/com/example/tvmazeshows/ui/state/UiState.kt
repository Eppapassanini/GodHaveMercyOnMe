package com.example.tvmazeshows.ui.state

sealed class UiState<out T> {
    object Loading : UiState<Nothing>()
    data class Content<T>(val data: T) : UiState<T>()
    data class Error(val message: String, val retryAction: (() -> Unit)? = null) : UiState<Nothing>()
}