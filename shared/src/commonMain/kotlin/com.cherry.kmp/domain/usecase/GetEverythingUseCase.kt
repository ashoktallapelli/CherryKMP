package com.cherry.kmp.domain.usecase

import com.cherry.kmp.domain.model.NewsRequest
import com.cherry.kmp.domain.model.NewsResults
import com.cherry.kmp.domain.model.QuotesResults
import com.cherry.kmp.domain.repository.Repository
import com.cherry.kmp.domain.usecase.base.UseCase
import kotlinx.coroutines.CoroutineDispatcher

class GetEverythingUseCase(
    private val repository: Repository, dispatcher: CoroutineDispatcher
) : UseCase<NewsRequest, NewsResults>(dispatcher) {

    override suspend fun execute(params: NewsRequest): NewsResults {
        return repository.getEverything(params)
    }
}