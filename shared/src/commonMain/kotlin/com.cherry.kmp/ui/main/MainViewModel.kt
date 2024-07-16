package com.cherry.kmp.ui.main

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cherry.kmp.data.local.entity.DataModelEntity
import com.cherry.kmp.domain.UiState
import com.cherry.kmp.domain.model.NewsRequest
import com.cherry.kmp.domain.model.NewsResults
import com.cherry.kmp.domain.model.Post
import com.cherry.kmp.domain.usecase.GetEverythingUseCase
import com.cherry.kmp.domain.usecase.GetPostsUseCase
import com.cherry.kmp.domain.usecase.GetTopHeadlinesUseCase
import com.cherry.kmp.domain.usecase.LocalDataUseCase
import kotlinx.coroutines.launch

class MainViewModel(
    private val getQuotesUseCase: GetPostsUseCase,
    private val localDataUseCase: LocalDataUseCase,
    private val getEverythingUseCase: GetEverythingUseCase,
    private val getTopHeadlinesUseCase: GetTopHeadlinesUseCase
) : ViewModel() {
    val uiState = mutableStateOf<UiState<List<Post>>>(UiState.Loading)
    fun loadItems() {
        viewModelScope.launch {
            getQuotesUseCase(Unit).collect { result ->
                uiState.value = result
            }
        }
    }

    val newsEverything = mutableStateOf<UiState<NewsResults>>(UiState.Loading)
    fun loadEverythingNews() {
        viewModelScope.launch {
            getEverythingUseCase(getEverythingRequest()).collect { result ->
                newsEverything.value = result
            }
        }
    }

    val newsHeadlines = mutableStateOf<UiState<NewsResults>>(UiState.Loading)
    fun loadHeadlinesNews() {
        viewModelScope.launch {
            getTopHeadlinesUseCase(getHeadlinesRequest()).collect { result ->
                newsHeadlines.value = result
            }
        }
    }

    val allItems = mutableStateOf<List<DataModelEntity>>(emptyList())
    val item = mutableStateOf(DataModelEntity(0L, ""))

    fun getAllValues() {
        viewModelScope.launch {
            localDataUseCase.getAllAsFlow().collect {
                allItems.value = it
            }
        }
    }

    fun insert(item: DataModelEntity) {
        viewModelScope.launch {
            localDataUseCase.insert(item)
        }
    }

    fun deleteAll() {
        viewModelScope.launch {
            localDataUseCase.deleteAll()
        }
    }

    fun getById(id: Long) {
        viewModelScope.launch {
            item.value = localDataUseCase.getById(id)
        }
    }

    private fun getEverythingRequest(): NewsRequest {
        return NewsRequest(query = "tesla", sortBy = "publishedAt")
    }

    private fun getHeadlinesRequest(): NewsRequest {
        return NewsRequest(country = "us", category = "business")
    }
}