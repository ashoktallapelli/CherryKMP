package com.cherry.kmp.domain.repository

import com.cherry.kmp.domain.model.NewsRequest
import com.cherry.kmp.domain.model.NewsResults
import com.cherry.kmp.domain.model.Post
import com.cherry.kmp.domain.model.QuotesResults

interface Repository {
    suspend fun getQuotes(): QuotesResults
    suspend fun getPosts(): List<Post>
    suspend fun getEverything(request: NewsRequest): NewsResults
    suspend fun getTopHeadlines(request: NewsRequest): NewsResults
}