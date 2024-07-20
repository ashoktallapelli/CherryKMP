package com.cherry.kmp.ui.splash

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.cherry.kmp.ui.navigation.SplashNavigation

@Composable
internal fun SplashNav(navigateToMain: () -> Unit) {
    val navigator = rememberNavController()

    NavHost(
        startDestination = SplashNavigation.Splash.route,
        navController = navigator,
        modifier = Modifier.fillMaxSize()
    ) {
        composable(route = SplashNavigation.Splash.route) {
            SplashScreen(
                navigateToMain = navigateToMain,
                navigateToLogin = {
                    navigator.popBackStack()
                    navigator.navigate(SplashNavigation.Login.route)
                })
        }
//        composable(route = SplashNavigation.Login.route) {
//            LoginScreen(
//                navigateToMain = navigateToMain, navigateToRegister = {
//                    navigator.navigate(SplashNavigation.Register.route)
//                },
//                state = viewModel.state.value,
//                events = viewModel::onTriggerEvent
//            )
//        }
//        composable(route = SplashNavigation.Register.route) {
//            RegisterScreen(
//                navigateToMain = navigateToMain, popUp = {
//                    navigator.popBackStack()
//                }, state = viewModel.state.value,
//                events = viewModel::onTriggerEvent
//            )
//        }
    }

}