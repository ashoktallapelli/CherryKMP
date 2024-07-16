package com.cherry.kmp.data.network

import com.cherry.kmp.data.network.ApiDefinition.ApiEndpoint.GetEverything
import com.cherry.kmp.data.network.ApiDefinition.ApiEndpoint.GetPosts
import com.cherry.kmp.data.network.ApiDefinition.ApiEndpoint.GetQuotes
import com.cherry.kmp.data.network.ApiDefinition.ApiEndpoint.GetTopHeadlines
import com.cherry.kmp.data.network.ApiDefinition.ApiField.PARAM_DOMAINS
import com.cherry.kmp.data.network.ApiDefinition.ApiField.PARAM_FROM
import com.cherry.kmp.data.network.ApiDefinition.ApiField.PARAM_QUERY
import com.cherry.kmp.data.network.ApiDefinition.ApiField.PARAM_SORT_BY
import com.cherry.kmp.data.network.ApiDefinition.ApiField.PARAM_SOURCES
import com.cherry.kmp.data.network.ApiDefinition.ApiField.PARAM_TO
import com.cherry.kmp.domain.model.NewsRequest
import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.request.parameter

class ApiService(private val client: HttpClient) {
    suspend fun getQuotes() = client.get(GetQuotes.path)
    suspend fun getPosts() = client.get(GetPosts.path)
    suspend fun getEverything(request: NewsRequest) = client.get(GetEverything.path) {
        request.query?.let { parameter(PARAM_QUERY, it) }
        request.from?.let { parameter(PARAM_FROM, it) }
        request.to?.let { parameter(PARAM_TO, it) }
        request.sortBy?.let { parameter(PARAM_SORT_BY, it) }
        request.sources?.let { parameter(PARAM_SOURCES, it) }
        request.domains?.let { parameter(PARAM_DOMAINS, it) }
    }

    suspend fun getTopHeadlines(request: NewsRequest) = client.get(GetTopHeadlines.path) {
        request.query?.let { parameter(PARAM_QUERY, it) }
        request.from?.let { parameter(PARAM_FROM, it) }
        request.to?.let { parameter(PARAM_TO, it) }
        request.sortBy?.let { parameter(PARAM_SORT_BY, it) }
        request.sources?.let { parameter(PARAM_SOURCES, it) }
        request.domains?.let { parameter(PARAM_DOMAINS, it) }
    }
}