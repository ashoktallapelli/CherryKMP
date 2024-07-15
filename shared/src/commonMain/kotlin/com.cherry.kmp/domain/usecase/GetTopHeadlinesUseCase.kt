package com.cherry.kmp.domain.usecase

import com.cherry.kmp.domain.model.NewsResults
import com.cherry.kmp.domain.repository.Repository
import com.cherry.kmp.domain.usecase.base.UseCase
import kotlinx.coroutines.CoroutineDispatcher

class GetTopHeadlinesUseCase(
    private val repository: Repository, dispatcher: CoroutineDispatcher
) : UseCase<Unit, NewsResults>(dispatcher) {

    override suspend fun execute(params: Unit): NewsResults {
        return repository.getTopHeadlines()
    }
}