package com.cherry.kmp.data.local

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import com.cherry.kmp.common.Clock
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

/**
 * Manages security-related preferences using DataStore
 */
class SecurityPreferences(private val dataStore: DataStore<Preferences>) {
    
    companion object {
        private val BIOMETRIC_ENABLED = booleanPreferencesKey("biometric_enabled")
        private val APP_LOCK_ENABLED = booleanPreferencesKey("app_lock_enabled") 
        private val LAST_AUTH_TIME = longPreferencesKey("last_auth_time")
        private val FIRST_TIME_SETUP = booleanPreferencesKey("first_time_setup")
    }
    
    /**
     * Check if biometric authentication is enabled
     */
    suspend fun isBiometricEnabled(): Boolean {
        return dataStore.data.map { preferences ->
            preferences[BIOMETRIC_ENABLED] ?: false
        }.first()
    }
    
    /**
     * Enable or disable biometric authentication
     */
    suspend fun setBiometricEnabled(enabled: Boolean) {
        dataStore.edit { preferences ->
            preferences[BIOMETRIC_ENABLED] = enabled
        }
    }
    
    /**
     * Check if app lock is enabled
     */
    suspend fun isAppLockEnabled(): Boolean {
        return dataStore.data.map { preferences ->
            preferences[APP_LOCK_ENABLED] ?: false
        }.first()
    }
    
    /**
     * Enable or disable app lock
     */
    suspend fun setAppLockEnabled(enabled: Boolean) {
        dataStore.edit { preferences ->
            preferences[APP_LOCK_ENABLED] = enabled
        }
    }
    
    /**
     * Get the last authentication time
     */
    suspend fun getLastAuthTime(): Long {
        return dataStore.data.map { preferences ->
            preferences[LAST_AUTH_TIME] ?: 0L
        }.first()
    }
    
    /**
     * Update the last authentication time
     */
    suspend fun updateLastAuthTime(time: Long = Clock.currentTimeMillis()) {
        dataStore.edit { preferences ->
            preferences[LAST_AUTH_TIME] = time
        }
    }
    
    /**
     * Check if this is the first time opening the app (for security setup)
     */
    suspend fun isFirstTimeSetup(): Boolean {
        return dataStore.data.map { preferences ->
            preferences[FIRST_TIME_SETUP] ?: true
        }.first()
    }
    
    /**
     * Mark that first-time setup is complete
     */
    suspend fun setFirstTimeSetupComplete() {
        dataStore.edit { preferences ->
            preferences[FIRST_TIME_SETUP] = false
        }
    }
    
    /**
     * Clear all security preferences (for reset/logout)
     */
    suspend fun clearAllSecuritySettings() {
        dataStore.edit { preferences ->
            preferences.remove(BIOMETRIC_ENABLED)
            preferences.remove(APP_LOCK_ENABLED)
            preferences.remove(LAST_AUTH_TIME)
            // Keep FIRST_TIME_SETUP so user doesn't see setup again
        }
    }
}