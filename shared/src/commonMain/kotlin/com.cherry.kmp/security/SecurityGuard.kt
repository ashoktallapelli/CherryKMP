package com.cherry.kmp.security

import com.cherry.kmp.common.LoggerConfig
import com.cherry.kmp.domain.usecase.SecuritySettingsUseCase

/**
 * Security guard that manages conditional authentication based on user preferences
 */
class SecurityGuard(
    private val biometricAuth: BiometricAuth,
    private val securitySettingsUseCase: SecuritySettingsUseCase
) {
    
    /**
     * Check if authentication is required and prompt user if needed
     * @param context What the user is trying to access (for better UX messaging)
     * @return AuthenticationResult indicating success, failure, or not needed
     */
    suspend fun requireAuthenticationIfNeeded(
        context: SecurityContext = SecurityContext.APP_ACCESS
    ): AuthenticationResult {
        
        try {
            // Check if biometric is enabled by user
            val isBiometricEnabled = securitySettingsUseCase.isBiometricEnabled()
            val isAppLockEnabled = securitySettingsUseCase.isAppLockEnabled()
            
            LoggerConfig.logger.d { "Security check - Biometric: $isBiometricEnabled, AppLock: $isAppLockEnabled, Context: $context" }
            
            // If no security is enabled, allow access
            if (!isBiometricEnabled && !isAppLockEnabled) {
                return AuthenticationResult.NotRequired
            }
            
            // Check if we need to re-authenticate due to timeout
            val shouldRequireAuth = when (context) {
                SecurityContext.APP_ACCESS -> {
                    // For app access, check app lock timeout
                    if (isAppLockEnabled) {
                        securitySettingsUseCase.shouldRequireAuth()
                    } else {
                        false
                    }
                }
                SecurityContext.SETTINGS_ACCESS,
                SecurityContext.PRIVATE_DATA -> {
                    // Always require auth for sensitive areas if biometric is enabled
                    isBiometricEnabled
                }
                SecurityContext.FIRST_TIME_SETUP -> {
                    // Don't require auth during setup
                    false
                }
            }
            
            if (!shouldRequireAuth) {
                // Update last auth time since user is accessing the app
                securitySettingsUseCase.updateLastAuthTime()
                return AuthenticationResult.NotRequired
            }
            
            // Prompt for authentication
            val authResult = biometricAuth.authenticate(
                title = getAuthTitle(context),
                subtitle = getAuthSubtitle(context),
                negativeButtonText = "Cancel"
            )
            
            return when (authResult) {
                BiometricResult.Success -> {
                    securitySettingsUseCase.updateLastAuthTime()
                    LoggerConfig.logger.i { "Authentication successful for $context" }
                    AuthenticationResult.Success
                }
                BiometricResult.UserCancelled -> {
                    LoggerConfig.logger.d { "User cancelled authentication for $context" }
                    AuthenticationResult.UserCancelled
                }
                BiometricResult.AuthenticationFailed -> {
                    LoggerConfig.logger.w { "Authentication failed for $context" }
                    AuthenticationResult.Failed("Authentication failed. Please try again.")
                }
                BiometricResult.BiometricNotAvailable -> {
                    LoggerConfig.logger.w { "Biometric not available, allowing access" }
                    // Fallback: allow access but log the issue
                    securitySettingsUseCase.updateLastAuthTime()
                    AuthenticationResult.Success
                }
                BiometricResult.BiometricNotEnrolled -> {
                    LoggerConfig.logger.w { "Biometric not enrolled" }
                    AuthenticationResult.Failed("Please set up biometrics in your device settings.")
                }
                is BiometricResult.Error -> {
                    LoggerConfig.logger.e { "Biometric error: ${authResult.message}" }
                    AuthenticationResult.Failed("Authentication error: ${authResult.message}")
                }
            }
            
        } catch (e: Exception) {
            LoggerConfig.logger.e(e) { "Security guard error for $context" }
            // On error, allow access but log it
            return AuthenticationResult.NotRequired
        }
    }
    
    /**
     * Quick check if any security features are enabled
     */
    suspend fun isSecurityEnabled(): Boolean {
        return try {
            securitySettingsUseCase.isBiometricEnabled() || 
            securitySettingsUseCase.isAppLockEnabled()
        } catch (e: Exception) {
            false
        }
    }
    
    /**
     * Force update last auth time (call when user successfully authenticates)
     */
    suspend fun markAuthenticated() {
        try {
            securitySettingsUseCase.updateLastAuthTime()
            LoggerConfig.logger.d { "Marked user as authenticated" }
        } catch (e: Exception) {
            LoggerConfig.logger.e(e) { "Failed to mark user as authenticated" }
        }
    }
    
    private fun getAuthTitle(context: SecurityContext): String {
        return when (context) {
            SecurityContext.APP_ACCESS -> "Welcome Back"
            SecurityContext.SETTINGS_ACCESS -> "Security Settings"
            SecurityContext.PRIVATE_DATA -> "Private Content"
            SecurityContext.FIRST_TIME_SETUP -> "Setup Security"
        }
    }
    
    private fun getAuthSubtitle(context: SecurityContext): String {
        return when (context) {
            SecurityContext.APP_ACCESS -> "Use your biometric to access CherryKMP"
            SecurityContext.SETTINGS_ACCESS -> "Authenticate to access security settings"
            SecurityContext.PRIVATE_DATA -> "Authenticate to view private content"
            SecurityContext.FIRST_TIME_SETUP -> "Set up biometric security for your app"
        }
    }
}

/**
 * Different contexts where authentication might be required
 */
enum class SecurityContext {
    APP_ACCESS,        // Opening the app after timeout
    SETTINGS_ACCESS,   // Accessing security settings
    PRIVATE_DATA,      // Viewing saved/private content
    FIRST_TIME_SETUP   // Initial security setup
}

/**
 * Result of authentication check
 */
sealed class AuthenticationResult {
    object Success : AuthenticationResult()
    object NotRequired : AuthenticationResult()
    object UserCancelled : AuthenticationResult()
    data class Failed(val message: String) : AuthenticationResult()
}