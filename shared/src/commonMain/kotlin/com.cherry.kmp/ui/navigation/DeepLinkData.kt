package com.cherry.kmp.ui.navigation

/**
 * Sealed class representing different types of deep link data
 * Used for cross-platform deep linking support
 */
sealed class DeepLinkData {
    data class Article(val articleId: Int) : DeepLinkData()
    data class Category(val category: String) : DeepLinkData()
    data class Source(val sourceId: String) : DeepLinkData()
    data class Search(val query: String, val category: String = "") : DeepLinkData()
    data class Section(val section: String) : DeepLinkData()
    
    companion object {
        /**
         * Parse a URL string into DeepLinkData
         * Used for cross-platform deep link parsing
         */
        fun fromUrl(url: String): DeepLinkData? {
            return try {
                // Remove scheme to get the rest of the URL
                val urlParts = url.split("://")
                if (urlParts.size < 2) return null
                
                val scheme = urlParts[0]
                val remainder = urlParts[1]
                
                // Handle different URL structures
                val pathAndQuery = when {
                    // Custom scheme like cherrykmp://article/123 (no host)
                    scheme == "cherrykmp" -> remainder
                    // HTTPS/HTTP like https://cherry.kmp/article/123 (with host)
                    else -> {
                        val parts = remainder.split("/", limit = 2)
                        if (parts.size < 2) return null
                        parts[1] // Skip the host part
                    }
                }
                
                val pathParts = pathAndQuery.split("?")
                val path = pathParts[0]
                val query = if (pathParts.size > 1) pathParts[1] else ""
                
                val segments = path.split("/").filter { it.isNotEmpty() }
                
                when {
                    // article/123
                    segments.size >= 2 && segments[0] == "article" -> {
                        val articleId = segments[1].toIntOrNull()
                        if (articleId != null) Article(articleId) else null
                    }
                    
                    // category/technology
                    segments.size >= 2 && segments[0] == "category" -> {
                        Category(segments[1])
                    }
                    
                    // source/bbc-news
                    segments.size >= 2 && segments[0] == "source" -> {
                        Source(segments[1])
                    }
                    
                    // search?query=kotlin&category=tech
                    segments.size >= 1 && segments[0] == "search" -> {
                        val queryParams = parseQueryParams(query)
                        val searchQuery = queryParams["query"]
                        if (searchQuery != null) {
                            Search(searchQuery, queryParams["category"] ?: "")
                        } else null
                    }
                    
                    // headlines, sources, everything
                    segments.size == 1 -> {
                        when (segments[0]) {
                            "headlines", "sources", "everything" -> Section(segments[0])
                            else -> null
                        }
                    }
                    
                    else -> null
                }
            } catch (e: Exception) {
                null
            }
        }
        
        private fun parseQueryParams(query: String): Map<String, String> {
            if (query.isEmpty()) return emptyMap()
            
            return query.split("&").mapNotNull { param ->
                val parts = param.split("=", limit = 2)
                if (parts.size == 2) {
                    parts[0] to parts[1]
                } else null
            }.toMap()
        }
    }
    
    /**
     * Convert DeepLinkData to navigation route
     */
    fun toNavigationRoute(): String {
        return when (this) {
            is Article -> NavigationRoutes.ArticleDetail.createRoute(articleId)
            is Category -> NavigationRoutes.CategoryNews.createRoute(category)
            is Source -> NavigationRoutes.SourceNews.createRoute(sourceId)
            is Search -> NavigationRoutes.SearchResults.createRoute(query, category)
            is Section -> {
                when (section) {
                    "headlines", "sources", "everything" -> NavigationRoutes.News.route
                    else -> NavigationRoutes.News.route
                }
            }
        }
    }
}