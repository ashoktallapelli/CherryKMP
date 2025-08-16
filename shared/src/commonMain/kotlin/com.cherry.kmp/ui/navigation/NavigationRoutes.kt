package com.cherry.kmp.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Article
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.Article
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.ui.graphics.vector.ImageVector

/**
 * Type-safe navigation routes with deep linking support
 */
sealed class NavigationRoutes(
    val route: String,
    val title: String = "",
    val selectedIcon: ImageVector? = null,
    val unSelectedIcon: ImageVector? = null,
) {
    
    // Main Tab Navigation
    data object Home : NavigationRoutes(
        route = "home",
        title = "Home",
        selectedIcon = Icons.Filled.Home,
        unSelectedIcon = Icons.Outlined.Home
    )
    
    data object News : NavigationRoutes(
        route = "news",
        title = "News",
        selectedIcon = Icons.Filled.Article,
        unSelectedIcon = Icons.Outlined.Article
    )
    
    data object Favorites : NavigationRoutes(
        route = "favorites",
        title = "Favorites",
        selectedIcon = Icons.Filled.Favorite,
        unSelectedIcon = Icons.Outlined.FavoriteBorder
    )
    
    data object Profile : NavigationRoutes(
        route = "profile",
        title = "Profile",
        selectedIcon = Icons.Filled.Person,
        unSelectedIcon = Icons.Outlined.Person
    )
    
    // Detail Navigation with Arguments
    data object ArticleDetail : NavigationRoutes(
        route = "article/{articleId}",
        title = "Article Details"
    ) {
        fun createRoute(articleId: Int): String = "article/$articleId"
        
        const val ARTICLE_ID_ARG = "articleId"
    }
    
    data object CategoryNews : NavigationRoutes(
        route = "category/{category}",
        title = "Category News"
    ) {
        fun createRoute(category: String): String = "category/$category"
        
        const val CATEGORY_ARG = "category"
    }
    
    data object SourceNews : NavigationRoutes(
        route = "source/{sourceId}",
        title = "Source News" 
    ) {
        fun createRoute(sourceId: String): String = "source/$sourceId"
        
        const val SOURCE_ID_ARG = "sourceId"
    }
    
    // Search with Query Parameters
    data object SearchResults : NavigationRoutes(
        route = "search?query={query}&category={category}",
        title = "Search Results"
    ) {
        fun createRoute(query: String, category: String = ""): String {
            return if (category.isNotEmpty()) {
                "search?query=$query&category=$category"
            } else {
                "search?query=$query"
            }
        }
        
        const val QUERY_ARG = "query"
        const val CATEGORY_ARG = "category"
    }
    
    // Settings Navigation
    data object EditProfile : NavigationRoutes(
        route = "editProfile",
        title = "Edit Profile"
    )
    
    data object SecuritySettings : NavigationRoutes(
        route = "securitySettings", 
        title = "Security Settings"
    )
    
    data object AppSettings : NavigationRoutes(
        route = "appSettings",
        title = "App Settings"
    )
    
    // Onboarding and Auth (for future use)
    data object Onboarding : NavigationRoutes(
        route = "onboarding",
        title = "Welcome"
    )
    
    data object Login : NavigationRoutes(
        route = "login",
        title = "Login"
    )
    
    // Error and Special Screens
    data object NotFound : NavigationRoutes(
        route = "notFound",
        title = "Page Not Found"
    )
    
    data object NoInternet : NavigationRoutes(
        route = "noInternet", 
        title = "No Internet"
    )
    
    companion object {
        // Main tab routes for bottom navigation
        val mainTabRoutes: List<NavigationRoutes> by lazy {
            listOf(
                Home,
                News,
                Favorites,
                Profile
            )
        }
        
        // All available routes for navigation validation
        val allRoutes: List<NavigationRoutes> by lazy {
            listOf(
                News, Profile,
                ArticleDetail, CategoryNews, SourceNews, SearchResults,
                EditProfile, SecuritySettings, AppSettings,
                Onboarding, Login, NotFound, NoInternet
            )
        }
        
        // Deep linkable routes (for external access)
        val deepLinkRoutes: List<NavigationRoutes> by lazy {
            listOf(
                News,
                ArticleDetail, CategoryNews, SourceNews, SearchResults
            )
        }
    }
}

/**
 * Navigation argument definitions for type safety
 */
object NavigationArguments {
    const val ARTICLE_ID = "articleId"
    const val CATEGORY = "category" 
    const val SOURCE_ID = "sourceId"
    const val QUERY = "query"
    const val USER_ID = "userId"
}

/**
 * Deep link URL patterns for external access
 */
object DeepLinkPatterns {
    const val BASE_URL = "https://cherry.kmp"
    const val CUSTOM_SCHEME = "cherrykmp"
    
    // Deep link patterns
    const val ARTICLE_DETAIL = "$BASE_URL/article/{articleId}"
    const val CATEGORY_NEWS = "$BASE_URL/category/{category}"
    const val SOURCE_NEWS = "$BASE_URL/source/{sourceId}"
    const val SEARCH_RESULTS = "$BASE_URL/search?query={query}"
    
    // Custom scheme patterns (for app-to-app communication)
    const val CUSTOM_ARTICLE = "$CUSTOM_SCHEME://article/{articleId}"
    const val CUSTOM_CATEGORY = "$CUSTOM_SCHEME://category/{category}"
}