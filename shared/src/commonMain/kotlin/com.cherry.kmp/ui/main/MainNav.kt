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
import androidx.compose.runtime.LaunchedEffect
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
import com.cherry.kmp.ui.navigation.NavigationRoutes
import com.cherry.kmp.ui.navigation.NavigationArgs
import com.cherry.kmp.ui.navigation.ArticleDetailArgs
import com.cherry.kmp.ui.navigation.NavigationManager
import com.cherry.kmp.ui.navigation.DeepLinkData
import com.cherry.kmp.ui.settings.SecuritySettingsScreen
import com.cherry.kmp.ui.screens.ArticleDetailScreen
import com.cherry.kmp.ui.theme.DefaultNavigationBarItemTheme

@Composable
fun MainNav(
    deepLinkData: DeepLinkData? = null,
    onLogout: () -> Unit
) {
    val navController = rememberNavController()
    val navigationManager = NavigationManager(navController)
    
    // Handle deep link navigation
    LaunchedEffect(deepLinkData) {
        deepLinkData?.let { linkData ->
            val route = linkData.toNavigationRoute()
            navController.navigate(route) {
                // Clear back stack to avoid navigation issues  
                popUpTo(navController.graph.startDestinationRoute ?: NavigationRoutes.Everything.route) {
                    saveState = false
                }
                launchSingleTop = true
            }
        }
    }
    val currentDestination =
        navController.currentBackStackEntryAsState().value?.destination

    val shouldShowBottomBar = when (currentDestination?.route) {
        NavigationRoutes.Everything.route,
        NavigationRoutes.Headlines.route,
        NavigationRoutes.Sources.route,
        NavigationRoutes.Profile.route -> true

        else -> false
    }
    Scaffold(bottomBar = {
        if (shouldShowBottomBar) {
            BottomNavigationUI(navController = navController)
        }
    }) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding)) {
            NavHost(
                startDestination = NavigationRoutes.Everything.route,
                navController = navController,
                modifier = Modifier.fillMaxSize()
            ) {
                // Main Tab Screens
                composable(route = NavigationRoutes.Everything.route) {
                    EverythingScreen(navController = navController)
                }
                composable(route = NavigationRoutes.Headlines.route) {
                    HeadlinesScreen(navController = navController)
                }
                composable(route = NavigationRoutes.Sources.route) {
                    SourcesScreen(navController = navController)
                }
                composable(route = NavigationRoutes.Profile.route) {
                    ProfileScreen(
                        navigateToEditProfile = {
                            navController.navigate(NavigationRoutes.EditProfile.route)
                        },
                        navigateToSecuritySettings = {
                            navController.navigate(NavigationRoutes.SecuritySettings.route)
                        }
                    )
                }
                
                // Detail Screens with Arguments
                composable(
                    route = NavigationRoutes.ArticleDetail.route,
                    arguments = listOf(NavigationArgs.articleIdArg)
                ) { backStackEntry ->
                    val args = ArticleDetailArgs.fromBackStackEntry(backStackEntry)
                    if (args != null) {
                        ArticleDetailScreen(
                            articleId = args.articleId,
                            navController = navController
                        )
                    } else {
                        // Handle invalid arguments - navigate back or show error
                        navigationManager.navigateBack()
                    }
                }
                
                // Settings Screens
                composable(route = NavigationRoutes.EditProfile.route) {
                    EditProfileScreen(navigateToProfile = {
                        navController.navigate(NavigationRoutes.Profile.route)
                    })
                }
                composable(route = NavigationRoutes.SecuritySettings.route) {
                    SecuritySettingsScreen(navController = navController)
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

            val items = NavigationRoutes.mainTabRoutes
            items.forEach { item ->
                NavigationBarItem(label = { Text(text = item.title) },
                    colors = DefaultNavigationBarItemTheme(),
                    selected = item.route == currentRoute,
                icon = {
                    (if (item.route == currentRoute) item.selectedIcon else item.unSelectedIcon)?.let { icon ->
                        Icon(
                            icon,
                            item.title
                        )
                    }
                },
                onClick = {
                    if (currentRoute != item.route) {
                        navController.navigate(item.route) {
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
