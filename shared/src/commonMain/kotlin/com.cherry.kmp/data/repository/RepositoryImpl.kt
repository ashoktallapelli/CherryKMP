package com.cherry.kmp.data.repository

import com.cherry.kmp.data.network.ApiService
import com.cherry.kmp.domain.model.NewsRequest
import com.cherry.kmp.domain.model.Post
import com.cherry.kmp.domain.repository.Repository
import io.ktor.client.call.body
import io.ktor.client.statement.HttpResponse

class RepositoryImpl(
    private val apiService: ApiService
) : Repository {
    override suspend fun getPosts(): List<Post> {
        return apiService.getPosts().body()
    }

    override suspend fun getEverything(request: NewsRequest): HttpResponse {
        return apiService.getEverything(request)
    }

    override suspend fun getTopHeadlines(request: NewsRequest): HttpResponse {
        return apiService.getTopHeadlines(request)
    }
}