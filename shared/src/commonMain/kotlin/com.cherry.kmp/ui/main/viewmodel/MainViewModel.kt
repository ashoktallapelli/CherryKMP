package com.cherry.kmp.ui.main.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import com.cherry.kmp.core.common.LoggerConfig
import com.cherry.kmp.data.local.entity.DataModelEntity
import com.cherry.kmp.domain.Constants
import com.cherry.kmp.core.domain.UiState
import com.cherry.kmp.core.domain.exception.ApiException
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

    // Track ongoing jobs for proper cleanup
    private var postsJob: Job? = null
    private var everythingJob: Job? = null
    private var headlinesJob: Job? = null
    private var localItemsJob: Job? = null

    // StateFlow for consistent state management across all UI states
    private val _postsUiState = MutableStateFlow<UiState<List<Post>>>(UiState.Initial)
    val postsUiState: StateFlow<UiState<List<Post>>> = _postsUiState.asStateFlow()
    
    private val _newsEverythingUiState = MutableStateFlow<UiState<NewsResults>>(UiState.Initial)
    val newsEverythingUiState: StateFlow<UiState<NewsResults>> = _newsEverythingUiState.asStateFlow()
    
    private val _newsHeadlinesUiState = MutableStateFlow<UiState<NewsResults>>(UiState.Initial)
    val newsHeadlinesUiState: StateFlow<UiState<NewsResults>> = _newsHeadlinesUiState.asStateFlow()
    
    // Standardized to StateFlow for consistency and better performance
    private val _allLocalItems = MutableStateFlow<List<DataModelEntity>>(emptyList())
    val allLocalItems: StateFlow<List<DataModelEntity>> = _allLocalItems.asStateFlow()
    
    private val _currentLocalItem = MutableStateFlow(DataModelEntity(0L, ""))
    val currentLocalItem: StateFlow<DataModelEntity> = _currentLocalItem.asStateFlow()

    fun loadPosts() {
        LoggerConfig.logger.d { "Loading posts..." }
        // Cancel any ongoing posts job
        postsJob?.cancel()
        postsJob = viewModelScope.launch {
            _postsUiState.value = UiState.Loading
            getPostsUseCase(Unit)
                .catch { exception ->
                    LoggerConfig.logger.e(exception) { "Error loading posts" }
                    _postsUiState.value = UiState.Error(
                        ApiException.NetworkException()
                    )
                }
                .collect { result ->
                    LoggerConfig.logger.d { "Posts loaded: ${if (result is UiState.Success) "Success with ${result.data.size} items" else result::class.simpleName}" }
                    _postsUiState.value = result
                }
        }
    }

    fun loadEverythingNews(request: NewsRequest = getEverythingRequest()) {
        // Cancel any ongoing everything news job
        everythingJob?.cancel()
        everythingJob = viewModelScope.launch {
            _newsEverythingUiState.value = UiState.Loading
            getEverythingUseCase(request)
                .catch { exception ->
                    LoggerConfig.logger.e(exception) { "Error loading everything news" }
                    _newsEverythingUiState.value = UiState.Error(
                        ApiException.NetworkException()
                    )
                }
                .collect { result ->
                    _newsEverythingUiState.value = result
                }
        }
    }

    fun loadHeadlinesNews() {
        // Cancel any ongoing headlines job
        headlinesJob?.cancel()
        headlinesJob = viewModelScope.launch {
            _newsHeadlinesUiState.value = UiState.Loading
            getTopHeadlinesUseCase(getHeadlinesRequest())
                .catch { exception ->
                    LoggerConfig.logger.e(exception) { "Error loading headlines" }
                    _newsHeadlinesUiState.value = UiState.Error(
                        ApiException.NetworkException()
                    )
                }
                .collect { result ->
                    _newsHeadlinesUiState.value = result
                }
        }
    }

    fun loadAllLocalItems() {
        // Cancel any ongoing local items job
        localItemsJob?.cancel()
        localItemsJob = viewModelScope.launch {
            localDataUseCase.getAllAsFlow()
                .catch { exception ->
                    LoggerConfig.logger.e(exception) { "Error loading local items" }
                    // For local data, we can keep the current state or show empty list
                    _allLocalItems.value = emptyList()
                }
                .collect { items ->
                    _allLocalItems.value = items
                }
        }
    }

    fun insertLocalItem(item: DataModelEntity) {
        viewModelScope.launch {
            try {
                localDataUseCase.insert(item)
                LoggerConfig.logger.d { "Successfully inserted local item: ${item.id}" }
            } catch (exception: Exception) {
                LoggerConfig.logger.e(exception) { "Error inserting local item" }
            }
        }
    }

    fun deleteAllLocalItems() {
        viewModelScope.launch {
            try {
                localDataUseCase.deleteAll()
                LoggerConfig.logger.d { "Successfully deleted all local items" }
            } catch (exception: Exception) {
                LoggerConfig.logger.e(exception) { "Error deleting local items" }
            }
        }
    }

    fun getLocalItemById(id: Long) {
        viewModelScope.launch {
            try {
                val item = localDataUseCase.getById(id) ?: DataModelEntity(0L, "")
                _currentLocalItem.value = item
                LoggerConfig.logger.d { "Retrieved local item: ${item.id}" }
            } catch (exception: Exception) {
                LoggerConfig.logger.e(exception) { "Error getting local item by id: $id" }
                _currentLocalItem.value = DataModelEntity(0L, "")
            }
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

    /**
     * Refresh all data - useful for pull-to-refresh scenarios
     */
    fun refreshAllData() {
        LoggerConfig.logger.d { "Refreshing all data..." }
        loadPosts()
        loadEverythingNews()
        loadHeadlinesNews()
        loadAllLocalItems()
    }

    /**
     * Cancel all ongoing operations - useful for early cleanup
     */
    fun cancelAllOperations() {
        LoggerConfig.logger.d { "Cancelling all ongoing operations..." }
        postsJob?.cancel()
        everythingJob?.cancel()
        headlinesJob?.cancel()
        localItemsJob?.cancel()
    }

    /**
     * Proper cleanup when ViewModel is cleared
     */
    override fun onCleared() {
        super.onCleared()
        LoggerConfig.logger.d { "MainViewModel cleared - cancelling ongoing operations" }
        cancelAllOperations()
    }
}