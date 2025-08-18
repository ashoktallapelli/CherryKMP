package com.cherry.kmp.ui.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cherry.kmp.core.common.LoggerConfig
import com.cherry.kmp.domain.usecase.SecuritySettingsUseCase
import com.cherry.kmp.security.BiometricAuth
import com.cherry.kmp.security.BiometricResult
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class SecuritySettingsUiState(
    val isBiometricEnabled: Boolean = false,
    val isBiometricAvailable: Boolean = false,
    val isAppLockEnabled: Boolean = false,
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)

class SecuritySettingsViewModel(
    private val securitySettingsUseCase: SecuritySettingsUseCase,
    private val biometricAuth: BiometricAuth
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(SecuritySettingsUiState())
    val uiState: StateFlow<SecuritySettingsUiState> = _uiState.asStateFlow()
    
    fun loadSecuritySettings() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            
            try {
                // Check biometric availability
                val isBiometricAvailable = biometricAuth.isBiometricAvailable()
                
                // Load user preferences
                val isBiometricEnabled = securitySettingsUseCase.isBiometricEnabled()
                val isAppLockEnabled = securitySettingsUseCase.isAppLockEnabled()
                
                _uiState.value = _uiState.value.copy(
                    isBiometricAvailable = isBiometricAvailable,
                    isBiometricEnabled = isBiometricEnabled && isBiometricAvailable,
                    isAppLockEnabled = isAppLockEnabled,
                    isLoading = false,
                    errorMessage = null
                )
                
                LoggerConfig.logger.d { "Security settings loaded: biometric=$isBiometricEnabled, appLock=$isAppLockEnabled" }
            } catch (e: Exception) {
                LoggerConfig.logger.e(e) { "Failed to load security settings" }
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = "Failed to load security settings"
                )
            }
        }
    }
    
    fun toggleBiometric(enabled: Boolean) {
        viewModelScope.launch {
            if (enabled) {
                // First check if user can authenticate
                val authResult = biometricAuth.authenticate(
                    title = "Enable Biometric Security",
                    subtitle = "Authenticate to enable biometric protection"
                )
                
                when (authResult) {
                    BiometricResult.Success -> {
                        securitySettingsUseCase.setBiometricEnabled(true)
                        _uiState.value = _uiState.value.copy(isBiometricEnabled = true)
                        LoggerConfig.logger.i { "Biometric authentication enabled" }
                    }
                    BiometricResult.UserCancelled -> {
                        // User decided not to enable, keep disabled
                        LoggerConfig.logger.d { "User cancelled biometric setup" }
                    }
                    BiometricResult.AuthenticationFailed -> {
                        _uiState.value = _uiState.value.copy(
                            errorMessage = "Authentication failed. Please try again."
                        )
                    }
                    BiometricResult.BiometricNotEnrolled -> {
                        _uiState.value = _uiState.value.copy(
                            errorMessage = "Please set up biometrics in your device settings first."
                        )
                    }
                    is BiometricResult.Error -> {
                        _uiState.value = _uiState.value.copy(
                            errorMessage = "Biometric setup failed: ${authResult.message}"
                        )
                    }
                    else -> {
                        _uiState.value = _uiState.value.copy(
                            errorMessage = "Biometric authentication is not available"
                        )
                    }
                }
            } else {
                // Disable biometric
                securitySettingsUseCase.setBiometricEnabled(false)
                _uiState.value = _uiState.value.copy(isBiometricEnabled = false)
                LoggerConfig.logger.i { "Biometric authentication disabled" }
            }
        }
    }
    
    fun toggleAppLock(enabled: Boolean) {
        viewModelScope.launch {
            try {
                securitySettingsUseCase.setAppLockEnabled(enabled)
                _uiState.value = _uiState.value.copy(isAppLockEnabled = enabled)
                LoggerConfig.logger.i { "App lock ${if (enabled) "enabled" else "disabled"}" }
            } catch (e: Exception) {
                LoggerConfig.logger.e(e) { "Failed to toggle app lock" }
                _uiState.value = _uiState.value.copy(
                    errorMessage = "Failed to update app lock setting"
                )
            }
        }
    }
    
    fun clearError() {
        _uiState.value = _uiState.value.copy(errorMessage = null)
    }
}