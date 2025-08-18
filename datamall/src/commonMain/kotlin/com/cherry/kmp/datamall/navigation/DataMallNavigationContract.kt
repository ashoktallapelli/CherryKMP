package com.cherry.kmp.datamall.navigation

import com.cherry.kmp.core.navigation.NavigationHandler

/**
 * Navigation contract specific to DataMall module
 * This defines the navigation capabilities needed by the DataMall module
 */
interface DataMallNavigationContract {
    
    /**
     * Navigate to the bus arrivals screen
     */
    fun navigateToBusArrivals()
    
    /**
     * Navigate to the MRT stations screen
     */
    fun navigateToMrtStations()
    
    /**
     * Navigate to the travel overview screen
     */
    fun navigateToTravelOverview()
    
    /**
     * Navigate to bus details with specific bus stop code
     */
    fun navigateToBusDetail(busStopCode: String)
    
    /**
     * Navigate to MRT details with specific station code
     */
    fun navigateToMrtDetail(stationCode: String)
    
    /**
     * Navigate back
     */
    fun navigateBack()
}

/**
 * Implementation that bridges the DataMall navigation contract with the core navigation handler
 */
class DataMallNavigationHandler(
    private val navigationHandler: NavigationHandler,
    private val routeProvider: DataMallRouteProvider
) : DataMallNavigationContract {
    
    override fun navigateToBusArrivals() {
        navigationHandler.navigate(routeProvider.busArrivalsRoute)
    }
    
    override fun navigateToMrtStations() {
        navigationHandler.navigate(routeProvider.mrtStationsRoute)
    }
    
    override fun navigateToTravelOverview() {
        navigationHandler.navigate(routeProvider.travelOverviewRoute)
    }
    
    override fun navigateToBusDetail(busStopCode: String) {
        navigationHandler.navigate(routeProvider.getBusDetailRoute(busStopCode))
    }
    
    override fun navigateToMrtDetail(stationCode: String) {
        navigationHandler.navigate(routeProvider.getMrtDetailRoute(stationCode))
    }
    
    override fun navigateBack() {
        navigationHandler.navigateBack()
    }
}

/**
 * Interface for providing route strings to the DataMall module
 * This will be implemented by the main app to provide actual route values
 */
interface DataMallRouteProvider {
    val busArrivalsRoute: String
    val mrtStationsRoute: String  
    val travelOverviewRoute: String
    
    /**
     * Get route for bus details with specific bus stop code
     */
    fun getBusDetailRoute(busStopCode: String): String
    
    /**
     * Get route for MRT details with specific station code
     */
    fun getMrtDetailRoute(stationCode: String): String
}