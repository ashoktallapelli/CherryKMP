package com.cherry.kmp.data.network


sealed class ApiDefinition {
    sealed class ApiEndpoint(val path: String) {
        data object GetPosts : ApiEndpoint(path = "/posts")
        data object GetQuotes : ApiEndpoint(path = "/quotes")
        data object GetEverything : ApiEndpoint(path = "/v2/everything")
        data object GetTopHeadlines : ApiEndpoint(path = "/v2/top-headlines")
    }

    object ApiField {
        const val PARAM_QUERY = "q"
        const val PARAM_FROM = "from"
        const val PARAM_TO = "to"
        const val PARAM_SORT_BY = "sortBy"
        const val PARAM_COUNTRY = "country"
        const val PARAM_CATEGORY = "category"
        const val PARAM_SOURCES = "sources"
        const val PARAM_DOMAINS = "domains"
    }
}