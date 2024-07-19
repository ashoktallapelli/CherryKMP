package com.cherry.kmp.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Newspaper
import androidx.compose.material.icons.filled.Source
import androidx.compose.material.icons.filled.ViewHeadline
import androidx.compose.material.icons.outlined.Newspaper
import androidx.compose.material.icons.outlined.Source
import androidx.compose.material.icons.outlined.ViewHeadline
import androidx.compose.ui.graphics.vector.ImageVector

sealed class MainNavigation(
    val route: String,
    val title: String,
    val selectedIcon: ImageVector,
    val unSelectedIcon: ImageVector,
) {

    data object Everything : MainNavigation(
        route = "everything", title = "Everything",
        selectedIcon = Icons.Filled.Newspaper,
        unSelectedIcon = Icons.Outlined.Newspaper
    )

    data object Headlines : MainNavigation(
        route = "headlines", title = "Headlines",
        selectedIcon = Icons.Filled.ViewHeadline,
        unSelectedIcon = Icons.Outlined.ViewHeadline
    )

    data object Sources : MainNavigation(
        route = "sources", title = "Sources",
        selectedIcon = Icons.Filled.Source,
        unSelectedIcon = Icons.Outlined.Source
    )

//    data object Profile : MainNavigation(
//        route = "profile", title = "Profile",
//        selectedIcon = Icons.Filled.Person,
//        unSelectedIcon = Icons.Outlined.Person
//    )
}

