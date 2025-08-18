package com.cherry.kmp.ui.navigation

import androidx.navigation.NavController
import com.cherry.kmp.core.navigation.NavigationHandler
import com.cherry.kmp.datamall.navigation.DataMallNavigationContract
import com.cherry.kmp.datamall.navigation.DataMallRouteProvider

/**
 * Main app implementation of NavigationHandler
 */
class MainNavigationHandler(
    private val navController: NavController
) : NavigationHandler {
    
    override fun navigate(route: String) {
        navController.navigate(route)
    }
    
    override fun navigateBack() {
        navController.navigateUp()
    }
    
    override fun navigateAndPopUpTo(route: String, popUpToRoute: String, inclusive: Boolean) {
        navController.navigate(route) {
            popUpTo(popUpToRoute) {
                this.inclusive = inclusive
            }
        }
    }
}

/**
 * Main app implementation of DataMallRouteProvider
 */
class MainDataMallRouteProvider : DataMallRouteProvider {
    override val busArrivalsRoute: String = NavigationRoutes.BusArrivals.route
    override val mrtStationsRoute: String = NavigationRoutes.MrtStations.route
    override val travelOverviewRoute: String = NavigationRoutes.TravelOverview.route
    
    override fun getBusDetailRoute(busStopCode: String): String {
        return "travel/bus/$busStopCode"
    }
    
    override fun getMrtDetailRoute(stationCode: String): String {
        return "travel/mrt/$stationCode"
    }
}