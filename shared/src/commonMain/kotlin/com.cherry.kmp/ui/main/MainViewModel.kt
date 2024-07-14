package com.cherry.kmp.ui.main

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cherry.kmp.data.entity.DataModelEntity
import com.cherry.kmp.domain.UiState
import com.cherry.kmp.domain.model.Post
import com.cherry.kmp.domain.usecase.GetPostsUseCase
import com.cherry.kmp.domain.usecase.LocalDataUseCase
import kotlinx.coroutines.launch

class MainViewModel(
    private val getQuotesUseCase: GetPostsUseCase,
    private val localDataUseCase: LocalDataUseCase
) : ViewModel() {
    val uiState = mutableStateOf<UiState<List<Post>>>(UiState.Loading)
    fun loadItems() {
        viewModelScope.launch {
            getQuotesUseCase(Unit).collect { result ->
                uiState.value = result
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
}