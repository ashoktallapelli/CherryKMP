package com.cherry.kmp.data.network

import com.cherry.kmp.data.network.ApiDefinition.ApiEndpoint.GetPosts
import com.cherry.kmp.data.network.ApiDefinition.ApiEndpoint.GetQuotes
import io.ktor.client.HttpClient
import io.ktor.client.request.get

class ApiService(private val client: HttpClient) {
    suspend fun getQuotes() = client.get(GetQuotes.path)
    suspend fun getPosts() = client.get(GetPosts.path)
}