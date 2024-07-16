package com.cherry.kmp.data.repository

import com.cherry.kmp.data.network.ApiService
import com.cherry.kmp.domain.model.NewsRequest
import com.cherry.kmp.domain.model.NewsResults
import com.cherry.kmp.domain.model.Post
import com.cherry.kmp.domain.model.QuotesResults
import com.cherry.kmp.domain.repository.Repository
import io.ktor.client.call.body
import io.ktor.http.Parameters
import io.ktor.http.parameters

class RepositoryImpl(
    private val apiService: ApiService
) : Repository {
    override suspend fun getQuotes(): QuotesResults {
        return apiService.getQuotes().body()
    }

    override suspend fun getPosts(): List<Post> {
        return apiService.getPosts().body()
    }

    override suspend fun getEverything(request: NewsRequest): NewsResults {
        parameters {

        }
        return apiService.getEverything(request).body()
    }

    override suspend fun getTopHeadlines(request: NewsRequest): NewsResults {
        return apiService.getTopHeadlines(request).body()
    }

    fun getParams(): Parameters {
        return parameters {

        }
    }
}