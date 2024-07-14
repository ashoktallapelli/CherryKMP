package com.cherry.kmp.domain.usecase

import com.cherry.kmp.domain.model.QuotesResults
import com.cherry.kmp.domain.repository.Repository
import com.cherry.kmp.domain.usecase.base.UseCase
import kotlinx.coroutines.CoroutineDispatcher

class GetQuotesUseCase(
    private val repository: Repository, dispatcher: CoroutineDispatcher
) : UseCase<Unit, QuotesResults>(dispatcher) {

    override suspend fun execute(params: Unit): QuotesResults {
        return repository.getQuotes()
    }
}