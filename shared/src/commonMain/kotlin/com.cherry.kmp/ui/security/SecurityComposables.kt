package com.cherry.kmp.ui.security

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Fingerprint
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.cherry.kmp.security.AuthenticationResult
import com.cherry.kmp.security.SecurityContext
import com.cherry.kmp.security.SecurityGuard
import org.koin.compose.koinInject

/**
 * Composable that wraps content with security authentication if needed
 */
@Composable
fun SecureContent(
    context: SecurityContext,
    modifier: Modifier = Modifier,
    securityGuard: SecurityGuard = koinInject(),
    onAuthenticationFailed: ((String) -> Unit)? = null,
    content: @Composable () -> Unit
) {
    var authState by remember { mutableStateOf<AuthenticationResult?>(null) }
    var isLoading by remember { mutableStateOf(true) }
    
    LaunchedEffect(context) {
        isLoading = true
        authState = securityGuard.requireAuthenticationIfNeeded(context)
        isLoading = false
    }
    
    Box(modifier = modifier) {
        when {
            isLoading -> {
                LoadingAuthScreen()
            }
            authState is AuthenticationResult.Success || authState is AuthenticationResult.NotRequired -> {
                content()
            }
            authState is AuthenticationResult.UserCancelled -> {
                AuthenticationCancelledScreen {
                    // Retry authentication
                    isLoading = true
                    authState = null
                }
            }
            authState is AuthenticationResult.Failed -> {
                AuthenticationFailedScreen(
                    message = (authState as AuthenticationResult.Failed).message,
                    onRetry = {
                        isLoading = true
                        authState = null
                    },
                    onCancel = {
                        onAuthenticationFailed?.invoke((authState as AuthenticationResult.Failed).message)
                    }
                )
            }
        }
    }
}

@Composable
private fun LoadingAuthScreen() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        CircularProgressIndicator(
            modifier = Modifier.size(48.dp),
            color = MaterialTheme.colorScheme.primary
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "Checking security...",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}

@Composable
private fun AuthenticationCancelledScreen(onRetry: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Card(
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant
            )
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    Icons.Default.Lock,
                    contentDescription = null,
                    modifier = Modifier.size(64.dp),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "Authentication Required",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "You need to authenticate to access this content.",
                    style = MaterialTheme.typography.bodyMedium,
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(24.dp))
                Button(onClick = onRetry) {
                    Icon(
                        Icons.Default.Fingerprint,
                        contentDescription = null,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.size(8.dp))
                    Text("Authenticate")
                }
            }
        }
    }
}

@Composable
private fun AuthenticationFailedScreen(
    message: String,
    onRetry: () -> Unit,
    onCancel: (() -> Unit)? = null
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Card(
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.errorContainer
            )
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    Icons.Default.Lock,
                    contentDescription = null,
                    modifier = Modifier.size(64.dp),
                    tint = MaterialTheme.colorScheme.onErrorContainer
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "Authentication Failed",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onErrorContainer
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = message,
                    style = MaterialTheme.typography.bodyMedium,
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onErrorContainer
                )
                Spacer(modifier = Modifier.height(24.dp))
                
                Column {
                    Button(onClick = onRetry) {
                        Text("Try Again")
                    }
                    if (onCancel != null) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Button(
                            onClick = onCancel,
                            colors = androidx.compose.material3.ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.surface
                            )
                        ) {
                            Text(
                                "Cancel",
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }
            }
        }
    }
}

/**
 * Simple biometric prompt composable for inline authentication
 */
@Composable
fun BiometricPrompt(
    title: String = "Authenticate",
    subtitle: String = "Use your biometric to continue",
    onResult: (AuthenticationResult) -> Unit,
    securityGuard: SecurityGuard = koinInject()
) {
    var isPrompting by remember { mutableStateOf(false) }
    
    if (!isPrompting) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Button(
                onClick = { isPrompting = true }
            ) {
                Icon(
                    Icons.Default.Fingerprint,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.size(8.dp))
                Text(title)
            }
        }
    }
    
    LaunchedEffect(isPrompting) {
        if (isPrompting) {
            val result = securityGuard.requireAuthenticationIfNeeded(SecurityContext.PRIVATE_DATA)
            onResult(result)
            isPrompting = false
        }
    }
}