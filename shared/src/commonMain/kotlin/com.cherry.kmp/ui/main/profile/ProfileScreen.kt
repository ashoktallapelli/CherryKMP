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
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.cherry.kmp.core.ui.theme.MinimalistColors
import com.cherry.kmp.ui.component.CircleImage
import kotlinx.coroutines.launch
import org.koin.compose.koinInject

@OptIn(ExperimentalMaterialApi::class)
@Composable
internal fun ProfileScreen(
    viewModel: ProfileViewModel = koinInject(),
    navigateToEditProfile: () -> Unit,
    navigateToSecuritySettings: () -> Unit = {},
) {
    LaunchedEffect(key1 = Unit) {
        viewModel.loadUserProfile()
    }
    val state by viewModel.uiState.collectAsState()
    val sheetState = androidx.compose.material.rememberModalBottomSheetState(initialValue = ModalBottomSheetValue.Hidden)
    val scope = rememberCoroutineScope()
    val scrollState = rememberScrollState()
    
    var isProfileLoaded by remember { mutableStateOf(false) }
    LaunchedEffect(state) {
        isProfileLoaded = true
    }
    
    val animationScale by animateFloatAsState(
        targetValue = if (isProfileLoaded) 1f else 0.8f,
        animationSpec = tween(
            durationMillis = 500,
            easing = FastOutSlowInEasing
        )
    )

    Scaffold(
        topBar = {
            ElegantProfileHeader()
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(paddingValues)
                .scale(animationScale)
        ) {
            // Combined Profile Header and Information
            CombinedProfileSection(
                state = state,
                onProfileImageClick = { scope.launch { sheetState.show() } },
                onEditClick = navigateToEditProfile
            )
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // Settings Section
            SettingsSection(
                onSecuritySettingsClick = navigateToSecuritySettings
            )
            
            Spacer(modifier = Modifier.height(100.dp)) // Bottom padding
        }
    }
}

@Composable
private fun ElegantProfileHeader() {
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
                .background(
                    Color.Transparent
                )
                .padding(horizontal = 24.dp, vertical = 20.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Left side - Profile title
                Column {
                    Text(
                        text = "Profile",
                        style = MaterialTheme.typography.headlineSmall.copy(
                            fontWeight = FontWeight.Bold
                        ),
                        color = MinimalistColors.PrimaryText
                    )
                    Text(
                        text = "Manage your account and preferences",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MinimalistColors.SecondaryText.copy(alpha = 0.7f)
                    )
                }
                
                // Right side - More options
                Card(
                    modifier = Modifier.size(48.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MinimalistColors.SecondarySurface
                    ),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.MoreVert,
                            contentDescription = "More options",
                            tint = MinimalistColors.PrimaryText.copy(alpha = 0.8f),
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun CombinedProfileSection(
    state: UserProfileState,
    onProfileImageClick: () -> Unit,
    onEditClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MinimalistColors.SecondarySurface
        ),
        shape = RoundedCornerShape(24.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            MinimalistColors.PrimaryText.copy(alpha = 0.05f),
                            MinimalistColors.SecondarySurface,
                            MinimalistColors.PrimarySurface
                        )
                    )
                )
                .padding(24.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(24.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Left Side - Profile Picture and Basic Info
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.weight(0.4f)
                ) {
                    // Profile Picture
                    CircleImage(
                        image = state.image,
                        modifier = Modifier.size(90.dp)
                    ) {
                        onProfileImageClick()
                    }
                    
                    Spacer(modifier = Modifier.height(12.dp))
                    
                    // Edit Profile Button
                    TextButton(
                        onClick = onEditClick,
                        modifier = Modifier.clip(RoundedCornerShape(16.dp))
                    ) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = null,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Edit",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Medium
                            )
                        )
                    }
                }
                
                // Right Side - Profile Information
                Column(
                    modifier = Modifier.weight(0.6f)
                ) {
                    // Name Display
                    Text(
                        text = state.name.ifBlank { "Add Your Name" },
                        style = MaterialTheme.typography.headlineSmall.copy(
                            fontWeight = FontWeight.Bold
                        ),
                        color = if (state.name.isBlank()) 
                            MinimalistColors.PrimaryText.copy(alpha = 0.6f) 
                        else 
                            MinimalistColors.PrimaryText
                    )
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    // Email Display
                    ProfileInfoRow(
                        icon = Icons.Default.Email,
                        value = state.email.ifBlank { "Add Your Email" },
                        isPlaceholder = state.email.isBlank()
                    )
                }
            }
        }
    }
}

@Composable
private fun ProfileInfoRow(
    icon: ImageVector,
    value: String,
    isPlaceholder: Boolean = false
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth()
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = MinimalistColors.PrimaryText.copy(alpha = 0.8f),
            modifier = Modifier.size(16.dp)
        )
        
        Spacer(modifier = Modifier.width(8.dp))
        
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            color = if (isPlaceholder) 
                MinimalistColors.PrimaryText.copy(alpha = 0.6f) 
            else 
                MinimalistColors.PrimaryText.copy(alpha = 0.8f)
        )
    }
}



@Composable
private fun SettingsSection(
    onSecuritySettingsClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
        colors = CardDefaults.cardColors(
            containerColor = MinimalistColors.SecondarySurface
        ),
        shape = RoundedCornerShape(20.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            Text(
                text = "Settings",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.SemiBold
                ),
                color = MinimalistColors.PrimaryText
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            SettingsItem(
                icon = Icons.Default.Security,
                title = "Privacy & Security",
                subtitle = "Biometric auth, encryption, and security settings",
                onClick = onSecuritySettingsClick
            )
            
            SettingsItem(
                icon = Icons.Default.Notifications,
                title = "Notifications",
                subtitle = "Push notifications and alert preferences",
                onClick = { /* TODO: Implement notifications settings */ }
            )
            
            SettingsItem(
                icon = Icons.Default.Settings,
                title = "App Preferences",
                subtitle = "Theme, language, and general settings",
                onClick = { /* TODO: Implement app settings */ }
            )
        }
    }
}

@Composable
private fun SettingsItem(
    icon: ImageVector,
    title: String,
    subtitle: String,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.Transparent
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = MinimalistColors.PrimaryText.copy(alpha = 0.8f),
                modifier = Modifier.size(24.dp)
            )
            
            Spacer(modifier = Modifier.width(16.dp))
            
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodyLarge.copy(
                        fontWeight = FontWeight.Medium
                    ),
                    color = MinimalistColors.PrimaryText
                )
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = MinimalistColors.PrimaryText.copy(alpha = 0.7f)
                )
            }
            
            Icon(
                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                contentDescription = null,
                tint = MinimalistColors.PrimaryText.copy(alpha = 0.5f),
                modifier = Modifier.size(20.dp)
            )
        }
    }
}