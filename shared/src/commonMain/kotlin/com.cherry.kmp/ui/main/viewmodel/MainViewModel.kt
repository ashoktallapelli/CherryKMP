package com.cherry.kmp.ui.main.viewmodel

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cherry.kmp.data.local.entity.DataModelEntity
import com.cherry.kmp.domain.Constants
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
    private val getPostsUseCase: GetPostsUseCase,
    private val localDataUseCase: LocalDataUseCase,
    private val getEverythingUseCase: GetEverythingUseCase,
    private val getTopHeadlinesUseCase: GetTopHeadlinesUseCase
) : ViewModel() {

    val postsUiState = mutableStateOf<UiState<List<Post>>>(UiState.Loading)
    val newsEverythingUiState = mutableStateOf<UiState<NewsResults>>(UiState.Loading)
    val newsHeadlinesUiState = mutableStateOf<UiState<NewsResults>>(UiState.Loading)
    val allLocalItems = mutableStateOf<List<DataModelEntity>>(emptyList())
    val currentLocalItem = mutableStateOf(DataModelEntity(0L, ""))

    fun loadPosts() {
        viewModelScope.launch {
            getPostsUseCase(Unit).collect { result ->
                postsUiState.value = result
            }
        }
    }

    fun loadEverythingNews(request: NewsRequest = getEverythingRequest()) {
        viewModelScope.launch {
            getEverythingUseCase(request).collect { result ->
                newsEverythingUiState.value = result
            }
        }
    }

    fun loadHeadlinesNews() {
        viewModelScope.launch {
            getTopHeadlinesUseCase(getHeadlinesRequest()).collect { result ->
                newsHeadlinesUiState.value = result
            }
        }
    }

    fun loadAllLocalItems() {
        viewModelScope.launch {
            localDataUseCase.getAllAsFlow().collect {
                allLocalItems.value = it
            }
        }
    }

    fun insertLocalItem(item: DataModelEntity) {
        viewModelScope.launch {
            localDataUseCase.insert(item)
        }
    }

    fun deleteAllLocalItems() {
        viewModelScope.launch {
            localDataUseCase.deleteAll()
        }
    }

    fun getLocalItemById(id: Long) {
        viewModelScope.launch {
            currentLocalItem.value = localDataUseCase.getById(id)
        }
    }

    private fun getEverythingRequest(): NewsRequest {
        return NewsRequest(query = Constants.QUERY_TELSA, sortBy = Constants.PUBLISHED_AT)
    }

    private fun getHeadlinesRequest(): NewsRequest {
        return NewsRequest(
            query = null,
            country = Constants.COUNTRY_INDIA
        )
    }

    fun getEverythingRequestWithSource(source: String): NewsRequest {
        return NewsRequest(query = null, sources = source)
    }
}