package com.cherry.kmp.ui.navigation

import androidx.navigation.NamedNavArgument

sealed class SplashNavigation(val route: String, val arguments: List<NamedNavArgument>) {
    data object Splash : SplashNavigation(route = "splash", arguments = emptyList())
    data object Login : SplashNavigation(route = "login", arguments = emptyList())
    data object Register : SplashNavigation(route = "register", arguments = emptyList())
}

