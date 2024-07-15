package com.cherry.kmp.data.repository

import com.cherry.kmp.data.network.ApiService
import com.cherry.kmp.domain.model.NewsResults
import com.cherry.kmp.domain.model.Post
import com.cherry.kmp.domain.model.QuotesResults
import com.cherry.kmp.domain.repository.Repository
import io.ktor.client.call.body

class RepositoryImpl(
    private val apiService: ApiService
) : Repository {
    override suspend fun getQuotes(): QuotesResults {
        return apiService.getQuotes().body()
    }

    override suspend fun getPosts(): List<Post> {
        return apiService.getPosts().body()
    }

    override suspend fun getEverything(): NewsResults {
        return apiService.getEverything().body()
    }

    override suspend fun getTopHeadlines(): NewsResults {
        return apiService.getTopHeadlines().body()
    }
}