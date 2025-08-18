package com.cherry.kmp.ui.navigation

import androidx.navigation.NavController
import androidx.navigation.NavOptionsBuilder
import com.cherry.kmp.core.common.LoggerConfig

/**
 * Navigation manager for type-safe navigation throughout the app
 */
class NavigationManager(private val navController: NavController) {
    
    /**
     * Navigate to a route with optional navigation options
     */
    fun navigateTo(
        route: String,
        builder: NavOptionsBuilder.() -> Unit = {}
    ) {
        try {
            navController.navigate(route, builder)
            LoggerConfig.logger.d { "Navigation: Navigated to $route" }
        } catch (e: Exception) {
            LoggerConfig.logger.e(e) { "Navigation: Failed to navigate to $route" }
            handleNavigationError(route, e)
        }
    }
    
    /**
     * Navigate to article detail with article ID
     */
    fun navigateToArticle(articleId: Int) {
        navigateTo(NavigationRoutes.ArticleDetail.createRoute(articleId))
    }
    
    /**
     * Navigate to category news
     */
    fun navigateToCategory(category: String) {
        navigateTo(NavigationRoutes.CategoryNews.createRoute(category))
    }
    
    /**
     * Navigate to source news
     */
    fun navigateToSource(sourceId: String) {
        navigateTo(NavigationRoutes.SourceNews.createRoute(sourceId))
    }
    
    /**
     * Navigate to search results
     */
    fun navigateToSearch(query: String, category: String = "") {
        navigateTo(NavigationRoutes.SearchResults.createRoute(query, category))
    }
    
    /**
     * Navigate to main tab with proper back stack management
     */
    fun navigateToMainTab(route: NavigationRoutes) {
        navigateTo(route.route) {
            // Clear back stack to start destination
            popUpTo(NavigationRoutes.News.route)
            launchSingleTop = true
            restoreState = true
        }
    }
    
    /**
     * Navigate back with validation
     */
    fun navigateBack(): Boolean {
        return try {
            if (navController.previousBackStackEntry != null) {
                navController.popBackStack()
                LoggerConfig.logger.d { "Navigation: Navigated back" }
                true
            } else {
                LoggerConfig.logger.d { "Navigation: No back stack entry, cannot navigate back" }
                false
            }
        } catch (e: Exception) {
            LoggerConfig.logger.e(e) { "Navigation: Failed to navigate back" }
            false
        }
    }
    
    /**
     * Clear entire navigation stack and go to start destination
     */
    fun navigateToStartAndClearStack() {
        navigateTo(NavigationRoutes.News.route) {
            popUpTo(NavigationRoutes.News.route) {
                inclusive = true
            }
            launchSingleTop = true
        }
    }
    
    /**
     * Get current route for state management
     */
    fun getCurrentRoute(): String? {
        return try {
            navController.currentBackStackEntry?.destination?.route
        } catch (e: Exception) {
            LoggerConfig.logger.e(e) { "Navigation: Failed to get current route" }
            null
        }
    }
    
    /**
     * Check if we can navigate back
     */
    fun canNavigateBack(): Boolean {
        return navController.previousBackStackEntry != null
    }
    
    /**
     * Handle navigation errors gracefully
     */
    private fun handleNavigationError(route: String, error: Exception) {
        LoggerConfig.logger.e(error) { "Navigation error for route: $route" }
        
        // Try to navigate to a safe fallback screen
        try {
            when {
                route.contains("article/") -> {
                    // If article navigation fails, go to news screen
                    navController.navigate(NavigationRoutes.News.route)
                }
                route.contains("category/") -> {
                    // If category navigation fails, go to news screen  
                    navController.navigate(NavigationRoutes.News.route)
                }
                route.contains("search") -> {
                    // If search navigation fails, go to news screen
                    navController.navigate(NavigationRoutes.News.route)
                }
                else -> {
                    // For any other error, try to go to start destination
                    navController.navigate(NavigationRoutes.News.route)
                }
            }
        } catch (fallbackError: Exception) {
            LoggerConfig.logger.e(fallbackError) { "Even fallback navigation failed" }
        }
    }
}

/**
 * Extension functions for easier navigation
 */
fun NavController.navigateToArticle(articleId: Int) {
    NavigationManager(this).navigateToArticle(articleId)
}

fun NavController.navigateToCategory(category: String) {
    NavigationManager(this).navigateToCategory(category)
}

fun NavController.navigateToSource(sourceId: String) {
    NavigationManager(this).navigateToSource(sourceId)
}

fun NavController.navigateToSearch(query: String, category: String = "") {
    NavigationManager(this).navigateToSearch(query, category)
}

fun NavController.safeNavigateBack(): Boolean {
    return NavigationManager(this).navigateBack()
}