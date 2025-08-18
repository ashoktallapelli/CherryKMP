package com.cherry.kmp.core.navigation

/**
 * Navigation contract for modular navigation
 * Each module can define their navigation needs through this interface
 */
interface NavigationHandler {
    /**
     * Navigate to a specific route
     */
    fun navigate(route: String)
    
    /**
     * Navigate back
     */
    fun navigateBack()
    
    /**
     * Navigate to a route and pop up to a specific destination
     */
    fun navigateAndPopUpTo(route: String, popUpToRoute: String, inclusive: Boolean = false)
}

/**
 * Interface for defining navigation routes in a module
 */
interface NavigationRoutes {
    val route: String
}

/**
 * Abstract navigation provider that modules can use to define their navigation needs
 */
abstract class ModuleNavigationProvider {
    
    /**
     * Get all navigation routes provided by this module
     */
    abstract fun getRoutes(): List<NavigationRoutes>
    
    /**
     * Handle navigation within the module
     */
    abstract fun handleInternalNavigation(handler: NavigationHandler, route: String): Boolean
}