package com.cherry.kmp.domain.usecase.base

import com.cherry.kmp.domain.UiState
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn

abstract class UseCase<in P, R>(private val dispatcher: CoroutineDispatcher) {
    protected abstract suspend fun execute(params: P): R

    operator fun invoke(params: P): Flow<UiState<R>> = flow {
        emit(UiState.Loading)
        try {
            val result = execute(params)
            emit(UiState.Success(result))
        } catch (e: Exception) {
            emit(UiState.Error(e))
        }
    }.flowOn(dispatcher)
}