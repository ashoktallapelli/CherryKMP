package com.cherry.kmp.ui.navigation

import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavType
import androidx.navigation.navArgument
import com.cherry.kmp.core.common.LoggerConfig

/**
 * Type-safe navigation argument definitions and validation
 */
object NavigationArgs {
    
    /**
     * Article ID argument definition
     */
    val articleIdArg = navArgument(NavigationArguments.ARTICLE_ID) {
        type = NavType.IntType
        nullable = false
        defaultValue = 0
    }
    
    /**
     * Category argument definition
     */
    val categoryArg = navArgument(NavigationArguments.CATEGORY) {
        type = NavType.StringType
        nullable = false
        defaultValue = ""
    }
    
    /**
     * Source ID argument definition
     */
    val sourceIdArg = navArgument(NavigationArguments.SOURCE_ID) {
        type = NavType.StringType
        nullable = false
        defaultValue = ""
    }
    
    /**
     * Search query argument definition
     */
    val queryArg = navArgument(NavigationArguments.QUERY) {
        type = NavType.StringType
        nullable = true
        defaultValue = ""
    }
    
    /**
     * User ID argument definition (for future use)
     */
    val userIdArg = navArgument(NavigationArguments.USER_ID) {
        type = NavType.IntType
        nullable = false
        defaultValue = 0
    }
}

/**
 * Extension functions for safe argument extraction
 */
fun NavBackStackEntry.getArticleId(): Int? {
    return try {
        arguments?.getInt(NavigationArguments.ARTICLE_ID)?.takeIf { it > 0 }
    } catch (e: Exception) {
        LoggerConfig.logger.e(e) { "Failed to get article ID from navigation arguments" }
        null
    }
}

fun NavBackStackEntry.getCategory(): String? {
    return try {
        arguments?.getString(NavigationArguments.CATEGORY)?.takeIf { it.isNotBlank() }
    } catch (e: Exception) {
        LoggerConfig.logger.e(e) { "Failed to get category from navigation arguments" }
        null
    }
}

fun NavBackStackEntry.getSourceId(): String? {
    return try {
        arguments?.getString(NavigationArguments.SOURCE_ID)?.takeIf { it.isNotBlank() }
    } catch (e: Exception) {
        LoggerConfig.logger.e(e) { "Failed to get source ID from navigation arguments" }
        null
    }
}

fun NavBackStackEntry.getSearchQuery(): String? {
    return try {
        arguments?.getString(NavigationArguments.QUERY)?.takeIf { it.isNotBlank() }
    } catch (e: Exception) {
        LoggerConfig.logger.e(e) { "Failed to get search query from navigation arguments" }
        null
    }
}

fun NavBackStackEntry.getUserId(): Int? {
    return try {
        arguments?.getInt(NavigationArguments.USER_ID)?.takeIf { it > 0 }
    } catch (e: Exception) {
        LoggerConfig.logger.e(e) { "Failed to get user ID from navigation arguments" }
        null
    }
}

/**
 * Validation functions for navigation arguments
 */
object NavigationValidator {
    
    fun isValidArticleId(articleId: Int?): Boolean {
        return articleId != null && articleId > 0
    }
    
    fun isValidCategory(category: String?): Boolean {
        val validCategories = listOf(
            "business", "entertainment", "general", "health", 
            "science", "sports", "technology"
        )
        return category != null && 
               category.isNotBlank() && 
               validCategories.contains(category.lowercase())
    }
    
    fun isValidSourceId(sourceId: String?): Boolean {
        return sourceId != null && 
               sourceId.isNotBlank() && 
               sourceId.length >= 2 &&
               sourceId.matches(Regex("^[a-zA-Z0-9-_.]+$"))
    }
    
    fun isValidSearchQuery(query: String?): Boolean {
        return query != null && 
               query.isNotBlank() && 
               query.trim().length >= 2 &&
               query.length <= 100
    }
    
    fun isValidUserId(userId: Int?): Boolean {
        return userId != null && userId > 0
    }
}

/**
 * Data classes for validated navigation arguments
 */
data class ArticleDetailArgs(
    val articleId: Int
) {
    companion object {
        fun fromBackStackEntry(entry: NavBackStackEntry): ArticleDetailArgs? {
            val articleId = entry.getArticleId()
            return if (NavigationValidator.isValidArticleId(articleId)) {
                ArticleDetailArgs(articleId!!)
            } else {
                LoggerConfig.logger.w { "Invalid article ID in navigation: $articleId" }
                null
            }
        }
    }
}

data class CategoryNewsArgs(
    val category: String
) {
    companion object {
        fun fromBackStackEntry(entry: NavBackStackEntry): CategoryNewsArgs? {
            val category = entry.getCategory()
            return if (NavigationValidator.isValidCategory(category)) {
                CategoryNewsArgs(category!!)
            } else {
                LoggerConfig.logger.w { "Invalid category in navigation: $category" }
                null
            }
        }
    }
}

data class SourceNewsArgs(
    val sourceId: String
) {
    companion object {
        fun fromBackStackEntry(entry: NavBackStackEntry): SourceNewsArgs? {
            val sourceId = entry.getSourceId()
            return if (NavigationValidator.isValidSourceId(sourceId)) {
                SourceNewsArgs(sourceId!!)
            } else {
                LoggerConfig.logger.w { "Invalid source ID in navigation: $sourceId" }
                null
            }
        }
    }
}

data class SearchResultsArgs(
    val query: String,
    val category: String = ""
) {
    companion object {
        fun fromBackStackEntry(entry: NavBackStackEntry): SearchResultsArgs? {
            val query = entry.getSearchQuery()
            val category = entry.getCategory() ?: ""
            
            return if (NavigationValidator.isValidSearchQuery(query)) {
                SearchResultsArgs(query!!, category)
            } else {
                LoggerConfig.logger.w { "Invalid search query in navigation: $query" }
                null
            }
        }
    }
}