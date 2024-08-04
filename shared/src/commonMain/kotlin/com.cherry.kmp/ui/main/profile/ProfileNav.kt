package com.cherry.kmp.ui.main.profile

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.cherry.kmp.ui.navigation.ProfileNavigation

@Composable
internal fun ProfileNav(navigateToMain: () -> Unit) {
    val navigator = rememberNavController()

    NavHost(
        startDestination = ProfileNavigation.Profile.route,
        navController = navigator,
        modifier = Modifier.fillMaxSize()
    ) {
        composable(route = ProfileNavigation.Profile.route) {
            ProfileScreen(navigateToEditProfile = {
                navigator.navigate(ProfileNavigation.EditProfile.route)
            })
        }
        composable(route = ProfileNavigation.EditProfile.route) {
            EditProfileScreen(navigateToProfile = {
                navigator.popBackStack()
            })
        }
    }
}