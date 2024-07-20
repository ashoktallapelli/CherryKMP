package com.cherry.kmp.domain

sealed interface UiState<out T> {
    object Initial : UiState<Nothing>
    object Loading : UiState<Nothing>
    data class Success<out T>(val data: T) : UiState<T>
    data class Error(val apiError: Throwable) : UiState<Nothing>
}