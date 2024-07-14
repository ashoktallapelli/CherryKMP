package com.cherry.kmp.data.network


sealed class ApiDefinition {
    sealed class ApiEndpoint(val path: String) {
        data object GetPosts : ApiEndpoint(path = "/posts")
        data object GetQuotes : ApiEndpoint(path = "/quotes")
    }

    object ApiField {
        const val PARAM_QUERY = "query"
        const val PARAM_PAGE = "page"
    }
}