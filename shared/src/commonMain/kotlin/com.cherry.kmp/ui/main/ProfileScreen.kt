package com.cherry.kmp.ui.main

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ModalBottomSheetValue
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MailOutline
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import cherrykmp.shared.generated.resources.Res
import cherrykmp.shared.generated.resources.cancel
import cherrykmp.shared.generated.resources.email
import cherrykmp.shared.generated.resources.name
import cherrykmp.shared.generated.resources.permission_description
import cherrykmp.shared.generated.resources.permission_required
import cherrykmp.shared.generated.resources.save
import cherrykmp.shared.generated.resources.settings
import com.cherry.kmp.common.PermissionCallback
import com.cherry.kmp.common.PermissionStatus
import com.cherry.kmp.common.PermissionType
import com.cherry.kmp.common.createPermissionsManager
import com.cherry.kmp.common.rememberCameraManager
import com.cherry.kmp.common.rememberGalleryManager
import com.cherry.kmp.common.toImageBitmap
import com.cherry.kmp.ui.component.CircleImage
import com.cherry.kmp.ui.component.GeneralAlertDialog
import com.cherry.kmp.ui.component.ImageOptionSheet
import com.cherry.kmp.ui.component.RoundedButton
import com.cherry.kmp.ui.component.Spacer_16dp
import com.cherry.kmp.ui.component.Spacer_64dp
import com.cherry.kmp.ui.component.UIComponentState
import com.cherry.kmp.ui.main.viewmodel.ProfileViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.koinInject

@OptIn(ExperimentalMaterialApi::class)
@Composable
internal fun ProfileScreen(
    viewModel: ProfileViewModel = koinInject(),
    navController: NavHostController
) {
    LaunchedEffect(key1 = Unit) {
        viewModel.load()
    }

    val state by viewModel.state.collectAsState()
    val coroutineScope = rememberCoroutineScope()

    var launchCamera by remember { mutableStateOf(value = false) }
    var launchGallery by remember { mutableStateOf(value = false) }
    var launchSetting by remember { mutableStateOf(value = false) }

    val permissionsManager = createPermissionsManager(object : PermissionCallback {
        override fun onPermissionStatus(
            permissionType: PermissionType,
            status: PermissionStatus
        ) {
            when (status) {
                PermissionStatus.GRANTED -> {
                    when (permissionType) {
                        PermissionType.CAMERA -> launchCamera = true
                        PermissionType.GALLERY -> launchGallery = true
                    }
                }

                else -> {
                    viewModel.setPermissionDialogState(UIComponentState.Show)
                }
            }
        }
    })


    val cameraManager = rememberCameraManager {
        coroutineScope.launch {
            val bitmap = withContext(Dispatchers.Default) {
                it?.toImageBitmap()
            }
            viewModel.setProfileImage(bitmap)
        }
    }

    val galleryManager = rememberGalleryManager {
        coroutineScope.launch {
            val bitmap = withContext(Dispatchers.Default) {
                it?.toByteArray()?.toImageBitmap()
            }
            viewModel.setProfileImage(bitmap)
        }
    }

    if (launchGallery) {
        if (permissionsManager.isPermissionGranted(PermissionType.GALLERY)) {
            galleryManager.launch()
        } else {
            permissionsManager.AskPermission(PermissionType.GALLERY)
        }
        launchGallery = false
    }

    if (launchCamera) {
        if (permissionsManager.isPermissionGranted(PermissionType.CAMERA)) {
            cameraManager.launch()
        } else {
            permissionsManager.AskPermission(PermissionType.CAMERA)
        }
        launchCamera = false
    }
    if (launchSetting) {
        permissionsManager.LaunchSettings()
        launchSetting = false
    }

    if (state.permissionDialog == UIComponentState.Show) {
        showPermissionDialog({ launchSetting = true }, {
            viewModel.setPermissionDialogState(UIComponentState.Hide)
        })
    }

    val sheetState =
        androidx.compose.material.rememberModalBottomSheetState(initialValue = ModalBottomSheetValue.Hidden)
    val scope = rememberCoroutineScope()

    androidx.compose.material.ModalBottomSheetLayout(
        sheetState = sheetState,
        sheetContent = {
            ImageOptionSheet(
                onGalleryClick = {
                    scope.launch { sheetState.hide() }
                    launchGallery = true
                },
                onCameraClick = {
                    scope.launch { sheetState.hide() }
                    launchCamera = true
                }
            )
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            // Profile Image
            CircleImage(image = state.image, modifier = Modifier.size(120.dp)) {
                scope.launch { sheetState.show() }
            }
            Spacer_16dp()
            // Name Field
            OutlinedTextField(
                value = state.name,
                onValueChange = {
                    viewModel.setName(it)
                },
                label = { Text(stringResource(Res.string.name)) },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = null
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
            )
            Spacer_16dp()
            // Email Field
            OutlinedTextField(
                value = state.email,
                onValueChange = { viewModel.setEmail(it) },
                label = { Text(stringResource(Res.string.email)) },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.MailOutline,
                        contentDescription = null
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
            )
            Spacer_64dp()
            // Save Button
            RoundedButton(text = stringResource(Res.string.save), image = Icons.Default.Save) {
                viewModel.saveUserProfile()
            }
        }
    }
}

@Composable
internal fun showPermissionDialog(onPositiveClick: () -> Unit, onDismiss: () -> Unit) {
    GeneralAlertDialog(title = stringResource(Res.string.permission_required),
        message = stringResource(Res.string.permission_description),
        positiveButtonText = stringResource(Res.string.settings),
        negativeButtonText = stringResource(Res.string.cancel),
        onDismissRequest = onDismiss,
        onPositiveClick = onPositiveClick,
        onNegativeClick = {
        })
}
