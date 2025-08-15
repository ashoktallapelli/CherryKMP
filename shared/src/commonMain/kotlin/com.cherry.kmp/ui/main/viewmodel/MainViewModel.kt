package com.cherry.kmp.ui.main.viewmodel

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import com.cherry.kmp.common.LoggerConfig
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

    // StateFlow for better performance and state management
    private val _postsUiState = MutableStateFlow<UiState<List<Post>>>(UiState.Initial)
    val postsUiState: StateFlow<UiState<List<Post>>> = _postsUiState.asStateFlow()
    
    private val _newsEverythingUiState = MutableStateFlow<UiState<NewsResults>>(UiState.Initial)
    val newsEverythingUiState: StateFlow<UiState<NewsResults>> = _newsEverythingUiState.asStateFlow()
    
    private val _newsHeadlinesUiState = MutableStateFlow<UiState<NewsResults>>(UiState.Initial)
    val newsHeadlinesUiState: StateFlow<UiState<NewsResults>> = _newsHeadlinesUiState.asStateFlow()
    
    // Keep local items as mutableStateOf for simple state
    val allLocalItems = mutableStateOf<List<DataModelEntity>>(emptyList())
    val currentLocalItem = mutableStateOf(DataModelEntity(0L, ""))

    fun loadPosts() {
        LoggerConfig.logger.d { "Loading posts..." }
        viewModelScope.launch {
            getPostsUseCase(Unit).collect { result ->
                LoggerConfig.logger.d { "Posts loaded: ${if (result is UiState.Success) "Success with ${result.data.size} items" else result::class.simpleName}" }
                _postsUiState.value = result
            }
        }
    }

    fun loadEverythingNews(request: NewsRequest = getEverythingRequest()) {
        viewModelScope.launch {
            getEverythingUseCase(request).collect { result ->
                _newsEverythingUiState.value = result
            }
        }
    }

    fun loadHeadlinesNews() {
        viewModelScope.launch {
            getTopHeadlinesUseCase(getHeadlinesRequest()).collect { result ->
                _newsHeadlinesUiState.value = result
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
            currentLocalItem.value = localDataUseCase.getById(id) ?: DataModelEntity(0L, "")
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