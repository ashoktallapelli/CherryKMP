package com.cherry.kmp.domain.usecase

import com.cherry.kmp.common.Clock
import com.cherry.kmp.data.local.SecurityPreferences

/**
 * Use case for managing security settings preferences
 */
class SecuritySettingsUseCase(
    private val securityPreferences: SecurityPreferences
) {
    
    companion object {
        private const val PREF_BIOMETRIC_ENABLED = "security_biometric_enabled"
        private const val PREF_APP_LOCK_ENABLED = "security_app_lock_enabled"
        private const val PREF_LAST_AUTH_TIME = "security_last_auth_time"
        private const val APP_LOCK_TIMEOUT_MINUTES = 5
    }
    
    /**
     * Check if biometric authentication is enabled by user
     */
    suspend fun isBiometricEnabled(): Boolean {
        return try {
            securityPreferences.isBiometricEnabled()
        } catch (e: Exception) {
            false
        }
    }
    
    /**
     * Enable or disable biometric authentication
     */
    suspend fun setBiometricEnabled(enabled: Boolean) {
        try {
            securityPreferences.setBiometricEnabled(enabled)
        } catch (e: Exception) {
            throw SecuritySettingsException("Failed to save biometric preference: ${e.message}")
        }
    }
    
    /**
     * Check if app lock is enabled by user
     */
    suspend fun isAppLockEnabled(): Boolean {
        return try {
            securityPreferences.isAppLockEnabled()
        } catch (e: Exception) {
            false
        }
    }
    
    /**
     * Enable or disable app lock
     */
    suspend fun setAppLockEnabled(enabled: Boolean) {
        try {
            securityPreferences.setAppLockEnabled(enabled)
        } catch (e: Exception) {
            throw SecuritySettingsException("Failed to save app lock preference: ${e.message}")
        }
    }
    
    /**
     * Update the last authentication time (for app lock timeout)
     */
    suspend fun updateLastAuthTime() {
        try {
            securityPreferences.updateLastAuthTime()
        } catch (e: Exception) {
            // Silently fail - not critical
        }
    }
    
    /**
     * Check if app lock timeout has expired
     */
    suspend fun shouldRequireAuth(): Boolean {
        return try {
            val appLockEnabled = isAppLockEnabled()
            if (!appLockEnabled) return false
            
            val lastAuthTime = securityPreferences.getLastAuthTime()
            val currentTime = Clock.currentTimeMillis()
            val timeoutMs = APP_LOCK_TIMEOUT_MINUTES * 60 * 1000L
            
            (currentTime - lastAuthTime) > timeoutMs
        } catch (e: Exception) {
            // On error, require auth for safety
            true
        }
    }
    
    /**
     * Get security settings summary for display
     */
    suspend fun getSecuritySummary(): SecuritySummary {
        return SecuritySummary(
            isBiometricEnabled = isBiometricEnabled(),
            isAppLockEnabled = isAppLockEnabled(),
            lastAuthTime = securityPreferences.getLastAuthTime()
        )
    }
    
    /**
     * Check if this is the first time opening the app
     */
    suspend fun isFirstTimeSetup(): Boolean {
        return try {
            securityPreferences.isFirstTimeSetup()
        } catch (e: Exception) {
            true // Default to first time if error
        }
    }
    
    /**
     * Mark first-time setup as complete
     */
    suspend fun setFirstTimeSetupComplete() {
        try {
            securityPreferences.setFirstTimeSetupComplete()
        } catch (e: Exception) {
            // Silently fail
        }
    }
}

/**
 * Security settings summary
 */
data class SecuritySummary(
    val isBiometricEnabled: Boolean,
    val isAppLockEnabled: Boolean,
    val lastAuthTime: Long
)

/**
 * Exception for security settings operations
 */
class SecuritySettingsException(message: String) : Exception(message)