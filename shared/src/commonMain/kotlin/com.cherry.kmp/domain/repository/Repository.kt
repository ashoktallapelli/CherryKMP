package com.cherry.kmp.domain.repository

import com.cherry.kmp.domain.model.NewsRequest
import com.cherry.kmp.domain.model.Post
import io.ktor.client.statement.HttpResponse

interface Repository {
    suspend fun getPosts(): List<Post>
    suspend fun getEverything(request: NewsRequest): HttpResponse
    suspend fun getTopHeadlines(request: NewsRequest): HttpResponse
}