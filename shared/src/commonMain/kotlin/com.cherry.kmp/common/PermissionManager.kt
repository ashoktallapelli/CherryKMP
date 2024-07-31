package com.cherry.kmp.common

import androidx.compose.runtime.Composable

expect class PermissionsManager(callback: PermissionCallback) : PermissionHandler

interface PermissionCallback {
    fun onPermissionStatus(permissionType: PermissionType, status: PermissionStatus)
}

@Composable
expect fun createPermissionsManager(callback: PermissionCallback): PermissionsManager

interface PermissionHandler {
    @Composable
    fun AskPermission(permission: PermissionType)

    @Composable
    fun isPermissionGranted(permission: PermissionType): Boolean

    @Composable
    fun LaunchSettings()
}

enum class PermissionType {
    CAMERA,
    GALLERY
}

enum class PermissionStatus {
    GRANTED,
    DENIED,
    SHOW_RATIONAL
}