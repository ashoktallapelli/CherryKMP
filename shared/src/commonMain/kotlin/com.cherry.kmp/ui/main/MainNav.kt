package com.cherry.kmp.ui.main

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.cherry.kmp.ui.theme.MinimalistColors
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
                popUpTo(navController.graph.startDestinationRoute ?: NavigationRoutes.Home.route) {
                    saveState = false
                }
                launchSingleTop = true
            }
        }
    }
    val currentDestination =
        navController.currentBackStackEntryAsState().value?.destination

    val shouldShowBottomBar = when (currentDestination?.route) {
        NavigationRoutes.Home.route,
        NavigationRoutes.News.route,
        NavigationRoutes.Favorites.route,
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
                startDestination = NavigationRoutes.Home.route,
                navController = navController,
                modifier = Modifier.fillMaxSize()
            ) {
                // Main Tab Screens
                composable(route = NavigationRoutes.Home.route) {
                    NewsScreen(navController = navController) // Using NewsScreen as Home for now
                }
                composable(route = NavigationRoutes.News.route) {
                    NewsScreen(navController = navController)
                }
                composable(route = NavigationRoutes.Favorites.route) {
                    // Placeholder for Favorites screen
                    NewsScreen(navController = navController) // Using NewsScreen as placeholder
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
    val items = NavigationRoutes.mainTabRoutes

    // Floating bottom navigation bar
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 24.dp),
        contentAlignment = Alignment.BottomCenter
    ) {
        Card(
            modifier = Modifier.wrapContentSize(),
            elevation = CardDefaults.cardElevation(defaultElevation = 12.dp),
            colors = CardDefaults.cardColors(
                containerColor = MinimalistColors.DeepCharcoal
            ),
            shape = RoundedCornerShape(32.dp)
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                items.forEach { item ->
                    val isSelected = item.route == currentRoute
                    
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .clip(CircleShape)
                            .background(
                                if (isSelected) 
                                    MinimalistColors.PureWhite 
                                else 
                                    Color.Transparent
                            )
                            .clickable {
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
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = if (isSelected) 
                                item.selectedIcon ?: item.unSelectedIcon!! 
                            else 
                                item.unSelectedIcon ?: item.selectedIcon!!,
                            contentDescription = item.title,
                            modifier = Modifier.size(20.dp),
                            tint = if (isSelected) 
                                MinimalistColors.DeepCharcoal 
                            else 
                                MinimalistColors.PureWhite
                        )
                    }
                }
            }
        }
    }
}
