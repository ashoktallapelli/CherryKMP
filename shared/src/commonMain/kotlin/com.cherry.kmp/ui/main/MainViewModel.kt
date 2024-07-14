package com.cherry.kmp.ui.main

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cherry.kmp.domain.UiState
import com.cherry.kmp.domain.model.Post
import com.cherry.kmp.domain.usecase.GetPostsUseCase
import kotlinx.coroutines.launch

class MainViewModel(private val getQuotesUseCase: GetPostsUseCase) : ViewModel() {
    val quotesResult = mutableStateOf<UiState<List<Post>>>(UiState.Loading)
    fun loadItems() {
        viewModelScope.launch {
            getQuotesUseCase(Unit).collect { result ->
                quotesResult.value = result
            }
        }
    }
}