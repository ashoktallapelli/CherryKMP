package com.cherry.kmp.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.cherry.kmp.ui.main.MainNav
import com.cherry.kmp.ui.navigation.AppNavigation
import com.cherry.kmp.ui.splash.SplashNav
import com.cherry.kmp.ui.theme.AppTheme

@Composable
internal fun App() {
    AppTheme {
        val navigator = rememberNavController()
        Box(modifier = Modifier.fillMaxSize()) {
            NavHost(
                navController = navigator,
                startDestination = AppNavigation.Splash.route,
                modifier = Modifier.fillMaxSize()
            ) {
                composable(route = AppNavigation.Splash.route) {
                    SplashNav(navigateToMain = {
                        navigator.popBackStack()
                        navigator.navigate(AppNavigation.Main.route)
                    })
                }
                composable(route = AppNavigation.Main.route) {
                    MainNav {
                        navigator.popBackStack()
                        navigator.navigate(AppNavigation.Splash.route)
                    }
                }
            }
        }
    }
}




