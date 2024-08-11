package com.cherry.kmp.ui.main

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.cherry.kmp.ui.main.profile.EditProfileScreen
import com.cherry.kmp.ui.main.profile.ProfileScreen
import com.cherry.kmp.ui.navigation.MainNavigation
import com.cherry.kmp.ui.theme.DefaultNavigationBarItemTheme

@Composable
fun MainNav(logout: () -> Unit) {

    val navController = rememberNavController()
    val currentDestination =
        navController.currentBackStackEntryAsState().value?.destination

    val shouldShowBottomBar = when (currentDestination?.route) {
        MainNavigation.Everything.route,
        MainNavigation.Headlines.route,
        MainNavigation.Sources.route,
        MainNavigation.Profile.route -> true

        else -> false
    }
    Scaffold(bottomBar = {
        if (shouldShowBottomBar) {
            BottomNavigationUI(navController = navController)
        }
    }) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding)) {
            NavHost(
                startDestination = MainNavigation.Everything.route,
                navController = navController,
                modifier = Modifier.fillMaxSize()
            ) {
                composable(route = MainNavigation.Everything.route) {
                    EverythingScreen(navController = navController)
                }
                composable(route = MainNavigation.Headlines.route) {
                    HeadlinesScreen(navController = navController)
                }
                composable(route = MainNavigation.Sources.route) {
                    SourcesScreen(navController = navController)
                }
                composable(route = MainNavigation.Profile.route) {
                    ProfileScreen(navigateToEditProfile = {
                        navController.navigate(MainNavigation.EditProfile.route)
                    })
                }
                composable(route = MainNavigation.EditProfile.route) {
                    EditProfileScreen(navigateToProfile = {
                        navController.navigate(MainNavigation.Profile.route)
                    })
                }
            }
        }

    }
}


@Composable
fun BottomNavigationUI(
    navController: NavController,
) {

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(10.dp),
        shape = RoundedCornerShape(
            topStart = 16.dp,
            topEnd = 16.dp
        )
    ) {
        NavigationBar(
            containerColor = MaterialTheme.colorScheme.background,
            contentColor = MaterialTheme.colorScheme.background,
            tonalElevation = 8.dp
        ) {

            val items = listOf(
                MainNavigation.Everything,
                MainNavigation.Headlines,
                MainNavigation.Sources,
                MainNavigation.Profile,
            )
            items.forEach {
                NavigationBarItem(label = { Text(text = it.title) },
                    colors = DefaultNavigationBarItemTheme(),
                    selected = it.route == currentRoute,
                    icon = {
                        (if (it.route == currentRoute) it.selectedIcon else it.unSelectedIcon)?.let { it1 ->
                            Icon(
                                it1,
                                it.title
                            )
                        }
                    },
                    onClick = {
                        if (currentRoute != it.route) {
                            navController.navigate(it.route) {
                                navController.graph.startDestinationRoute?.let { route ->
                                    popUpTo(route) {
                                        saveState = true
                                    }
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    })
            }
        }
    }
}
