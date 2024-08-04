package com.cherry.kmp.ui.navigation

import androidx.navigation.NamedNavArgument

sealed class ProfileNavigation(val route: String, val arguments: List<NamedNavArgument>) {
    data object Profile : ProfileNavigation(route = "profile", arguments = emptyList())
    data object EditProfile : ProfileNavigation(route = "edit_profile", arguments = emptyList())
}