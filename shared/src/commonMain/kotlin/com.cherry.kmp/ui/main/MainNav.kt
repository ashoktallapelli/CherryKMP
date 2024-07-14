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
import com.cherry.kmp.ui.navigation.MainNavigation
import com.cherry.kmp.ui.theme.DefaultNavigationBarItemTheme

@Composable
fun MainNav(logout: () -> Unit) {

    val navBottomBarController = rememberNavController()
    Scaffold(bottomBar = {
        BottomNavigationUI(navController = navBottomBarController)
    }) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding)) {
            NavHost(
                startDestination = MainNavigation.Everything.route,
                navController = navBottomBarController,
                modifier = Modifier.fillMaxSize()
            ) {
                composable(route = MainNavigation.Everything.route) {
                    RemoteDataScreen(navController = navBottomBarController)
                }
                composable(route = MainNavigation.Headlines.route) {
                    LocalDataScreen(navController = navBottomBarController)
                }
                composable(route = MainNavigation.Sources.route) {
//                    CartNav()
                }
                composable(route = MainNavigation.Profile.route) {
//                    ProfileNav(logout = logout)
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
                        Icon(
                            if (it.route == currentRoute) it.selectedIcon else it.unSelectedIcon,
                            it.title
                        )
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
