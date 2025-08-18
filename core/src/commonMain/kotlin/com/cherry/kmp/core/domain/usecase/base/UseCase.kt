package com.cherry.kmp.core.domain.usecase.base

import com.cherry.kmp.core.domain.UiState
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn

abstract class UseCase<in P, R>(private val dispatcher: CoroutineDispatcher) {
    private var cachedResult: R? = null
    
    protected abstract suspend fun execute(params: P): R

    operator fun invoke(params: P): Flow<UiState<R>> = flow {
        emit(UiState.Loading)
        try {
            val result = execute(params)
            cachedResult = result  // Simple cache
            emit(UiState.Success(result))
        } catch (e: Exception) {
            // Try to provide cached data if available
            cachedResult?.let { cached ->
                emit(UiState.CachedSuccess(cached))
            } ?: emit(UiState.Error(e))
        }
    }.flowOn(dispatcher)
}