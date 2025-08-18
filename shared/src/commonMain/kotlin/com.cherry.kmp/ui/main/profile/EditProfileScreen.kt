package com.cherry.kmp.ui.main.profile

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ModalBottomSheetValue
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.cherry.kmp.core.ui.theme.MinimalistColors
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
import com.cherry.kmp.ui.component.UIComponentState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.koinInject

@OptIn(ExperimentalMaterialApi::class)
@Composable
internal fun EditProfileScreen(
    viewModel: ProfileViewModel = koinInject(),
    navigateToProfile: () -> Unit,
) {
    LaunchedEffect(key1 = Unit) {
        viewModel.loadUserProfile()
    }

    val state by viewModel.uiState.collectAsState()
    val coroutineScope = rememberCoroutineScope()

    var launchCamera by remember { mutableStateOf(value = false) }
    var launchGallery by remember { mutableStateOf(value = false) }
    var launchSetting by remember { mutableStateOf(value = false) }
    var isSaving by remember { mutableStateOf(false) }
    
    var isScreenLoaded by remember { mutableStateOf(false) }
    LaunchedEffect(state) {
        isScreenLoaded = true
    }
    
    val screenAnimationScale by animateFloatAsState(
        targetValue = if (isScreenLoaded) 1f else 0.95f,
        animationSpec = tween(
            durationMillis = 400,
            easing = FastOutSlowInEasing
        )
    )

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
                    viewModel.updatePermissionDialogState(UIComponentState.Show)
                }
            }
        }
    })


    val cameraManager = rememberCameraManager {
        coroutineScope.launch {
            val bitmap = withContext(Dispatchers.Default) {
                it?.toImageBitmap()
            }
            viewModel.updateProfileImage(bitmap)
        }
    }

    val galleryManager = rememberGalleryManager {
        coroutineScope.launch {
            val bitmap = withContext(Dispatchers.Default) {
                it?.toByteArray()?.toImageBitmap()
            }
            viewModel.updateProfileImage(bitmap)
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
            viewModel.updatePermissionDialogState(UIComponentState.Hide)
        })
    }

    val sheetState =
        androidx.compose.material.rememberModalBottomSheetState(initialValue = ModalBottomSheetValue.Hidden)
    val scope = rememberCoroutineScope()

    Scaffold(
        topBar = {
            ElegantEditProfileHeader(
                onBackClick = { navigateToProfile() }
            )
        },
        content = { paddingValues ->
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
                        .verticalScroll(rememberScrollState())
                        .padding(paddingValues)
                        .padding(16.dp)
                        .scale(screenAnimationScale),
                    verticalArrangement = Arrangement.spacedBy(24.dp)
                ) {
                    // Profile Image Section
                    ProfileImageCard(
                        image = state.image,
                        onImageClick = { scope.launch { sheetState.show() } }
                    )
                    
                    // Form Fields Section
                    ProfileFormCard(
                        name = state.name,
                        email = state.email,
                        onNameChange = viewModel::updateName,
                        onEmailChange = viewModel::updateEmail
                    )
                    
                    // Save Action Section
                    SaveActionSection(
                        isSaving = isSaving,
                        isFormValid = state.name.isNotBlank() && state.name.length >= 2 && state.email.isNotBlank() && isValidEmail(state.email),
                        onSave = {
                            if (state.name.isNotBlank() && state.email.isNotBlank() && isValidEmail(state.email)) {
                                isSaving = true
                                coroutineScope.launch {
                                    viewModel.saveUserProfile()
                                    isSaving = false
                                    navigateToProfile()
                                }
                            }
                        }
                    )
                    
                    Spacer(modifier = Modifier.height(50.dp)) // Bottom padding
                }
            }
        }
    )
}

@Composable
private fun ElegantEditProfileHeader(
    onBackClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        colors = CardDefaults.cardColors(
            containerColor = MinimalistColors.PrimarySurface
        ),
        shape = RoundedCornerShape(
            bottomStart = 24.dp,
            bottomEnd = 24.dp
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.Transparent)
                .padding(horizontal = 24.dp, vertical = 20.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Left side - Back button and title
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Card(
                        modifier = Modifier.size(48.dp),
                        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MinimalistColors.SecondarySurface
                        ),
                        shape = RoundedCornerShape(16.dp),
                        onClick = onBackClick
                    ) {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Back",
                                tint = MinimalistColors.PrimaryText.copy(alpha = 0.8f),
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                    
                    Column {
                        Text(
                            text = "Edit Profile",
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.Bold
                            ),
                            color = MinimalistColors.PrimaryText
                        )
                        Text(
                            text = "Update your personal information",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MinimalistColors.SecondaryText.copy(alpha = 0.7f)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ProfileImageCard(
    image: androidx.compose.ui.graphics.ImageBitmap?,
    onImageClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MinimalistColors.SecondarySurface
        ),
        shape = RoundedCornerShape(24.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.Transparent)
                .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Profile Photo",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.SemiBold
                ),
                color = MinimalistColors.PrimaryText
            )
            
            Spacer(modifier = Modifier.height(20.dp))
            
            CircleImage(
                image = image,
                modifier = Modifier.size(100.dp)
            ) {
                onImageClick()
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            Text(
                text = "Tap to change photo",
                style = MaterialTheme.typography.bodySmall,
                color = MinimalistColors.PrimaryText.copy(alpha = 0.6f),
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
private fun ProfileFormCard(
    name: String,
    email: String,
    onNameChange: (String) -> Unit,
    onEmailChange: (String) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
        colors = CardDefaults.cardColors(
            containerColor = MinimalistColors.SecondarySurface
        ),
        shape = RoundedCornerShape(20.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            Text(
                text = "Personal Information",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.SemiBold
                ),
                color = MinimalistColors.PrimaryText
            )
            
            // Name Field
            OutlinedTextField(
                value = name,
                onValueChange = onNameChange,
                label = { Text(stringResource(Res.string.name)) },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = null,
                        tint = MinimalistColors.PrimaryText.copy(alpha = 0.8f)
                    )
                },
                isError = name.isNotBlank() && name.length < 2,
                supportingText = {
                    if (name.isNotBlank() && name.length < 2) {
                        Text(
                            text = "Name must be at least 2 characters",
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MinimalistColors.PrimaryText,
                    unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f),
                    focusedContainerColor = MaterialTheme.colorScheme.surface,
                    unfocusedContainerColor = MaterialTheme.colorScheme.surfaceContainerLow
                )
            )
            
            // Email Field  
            OutlinedTextField(
                value = email,
                onValueChange = onEmailChange,
                label = { Text(stringResource(Res.string.email)) },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Email,
                        contentDescription = null,
                        tint = MinimalistColors.PrimaryText.copy(alpha = 0.8f)
                    )
                },
                isError = email.isNotBlank() && !isValidEmail(email),
                supportingText = {
                    if (email.isNotBlank() && !isValidEmail(email)) {
                        Text(
                            text = "Please enter a valid email address",
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MinimalistColors.PrimaryText,
                    unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f),
                    focusedContainerColor = MaterialTheme.colorScheme.surface,
                    unfocusedContainerColor = MaterialTheme.colorScheme.surfaceContainerLow
                )
            )
        }
    }
}

@Composable
private fun SaveActionSection(
    isSaving: Boolean,
    isFormValid: Boolean,
    onSave: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (isSaving) {
            LinearProgressIndicator(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                color = MinimalistColors.DeepCharcoal
            )
        }
        
        Button(
            onClick = onSave,
            enabled = isFormValid && !isSaving,
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = MinimalistColors.DeepCharcoal,
                contentColor = MinimalistColors.PureWhite,
                disabledContainerColor = MinimalistColors.LightGrey,
                disabledContentColor = MinimalistColors.DeepCharcoal.copy(alpha = 0.6f)
            )
        ) {
            if (isSaving) {
                androidx.compose.material3.CircularProgressIndicator(
                    modifier = Modifier.size(18.dp),
                    strokeWidth = 2.dp,
                    color = MinimalistColors.PureWhite
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Saving...",
                    style = MaterialTheme.typography.labelLarge.copy(
                        fontWeight = FontWeight.Medium
                    )
                )
            } else {
                Icon(
                    imageVector = Icons.Default.Save,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = stringResource(Res.string.save),
                    style = MaterialTheme.typography.labelLarge.copy(
                        fontWeight = FontWeight.Medium
                    )
                )
            }
        }
        
        if (!isFormValid && !isSaving) {
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = "Please fill in all required fields correctly",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.error,
                textAlign = TextAlign.Center
            )
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

private fun isValidEmail(email: String): Boolean {
    return email.contains("@") && email.contains(".") && email.length > 5
}
