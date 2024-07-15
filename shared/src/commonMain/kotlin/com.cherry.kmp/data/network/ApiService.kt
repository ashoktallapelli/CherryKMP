package com.cherry.kmp.data.network

import com.cherry.kmp.data.network.ApiDefinition.ApiEndpoint.GetEverything
import com.cherry.kmp.data.network.ApiDefinition.ApiEndpoint.GetPosts
import com.cherry.kmp.data.network.ApiDefinition.ApiEndpoint.GetQuotes
import com.cherry.kmp.data.network.ApiDefinition.ApiEndpoint.GetTopHeadlines
import com.cherry.kmp.data.network.ApiDefinition.ApiField.PARAM_QUERY
import com.cherry.kmp.data.network.ApiDefinition.ApiField.PARAM_SORT_BY
import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.http.parameters

class ApiService(private val client: HttpClient) {
    suspend fun getQuotes() = client.get(GetQuotes.path)
    suspend fun getPosts() = client.get(GetPosts.path)
    suspend fun getEverything() = client.get(GetEverything.path) {
        parameters {  }
        parameter(PARAM_QUERY, "apple")
        parameter(PARAM_SORT_BY, "publishedAt")
    }

    suspend fun getTopHeadlines() = client.get(GetTopHeadlines.path)
}