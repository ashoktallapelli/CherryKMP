package com.cherry.kmp.security

/**
 * Common interface for biometric authentication across platforms
 */
expect class BiometricAuth {
    
    /**
     * Check if biometric authentication is available on this device
     */
    suspend fun isBiometricAvailable(): Boolean
    
    /**
     * Check if user has enrolled biometrics (fingerprint, face, etc.)
     */
    suspend fun hasBiometricEnrolled(): Boolean
    
    /**
     * Authenticate user using biometrics
     * @return true if authentication successful, false otherwise
     */
    suspend fun authenticate(
        title: String = "Biometric Authentication",
        subtitle: String = "Use your biometric to authenticate",
        negativeButtonText: String = "Cancel"
    ): BiometricResult
}

/**
 * Result of biometric authentication
 */
sealed class BiometricResult {
    object Success : BiometricResult()
    object UserCancelled : BiometricResult()
    object AuthenticationFailed : BiometricResult()
    object BiometricNotAvailable : BiometricResult()
    object BiometricNotEnrolled : BiometricResult()
    data class Error(val message: String) : BiometricResult()
}

/**
 * Security manager that combines encryption and biometric authentication
 */
class SecurityManager(
    private val biometricAuth: BiometricAuth,
    private val dataEncryption: DataEncryption
) {
    
    /**
     * Authenticate user and then encrypt sensitive data
     */
    suspend fun secureEncrypt(data: String): SecureOperationResult<String> {
        // First authenticate with biometrics
        val authResult = biometricAuth.authenticate(
            title = "Secure Data",
            subtitle = "Authenticate to encrypt sensitive data"
        )
        
        return when (authResult) {
            BiometricResult.Success -> {
                try {
                    val encrypted = dataEncryption.encryptString(data)
                    SecureOperationResult.Success(encrypted)
                } catch (e: Exception) {
                    SecureOperationResult.Error("Encryption failed: ${e.message}")
                }
            }
            BiometricResult.UserCancelled -> SecureOperationResult.UserCancelled
            BiometricResult.AuthenticationFailed -> SecureOperationResult.AuthenticationFailed
            BiometricResult.BiometricNotAvailable -> {
                // Fallback to encryption without biometrics
                try {
                    val encrypted = dataEncryption.encryptString(data)
                    SecureOperationResult.Success(encrypted)
                } catch (e: Exception) {
                    SecureOperationResult.Error("Encryption failed: ${e.message}")
                }
            }
            BiometricResult.BiometricNotEnrolled -> SecureOperationResult.BiometricNotEnrolled
            is BiometricResult.Error -> SecureOperationResult.Error(authResult.message)
        }
    }
    
    /**
     * Authenticate user and then decrypt sensitive data
     */
    suspend fun secureDecrypt(encryptedData: String): SecureOperationResult<String> {
        // First authenticate with biometrics if available
        if (biometricAuth.isBiometricAvailable()) {
            val authResult = biometricAuth.authenticate(
                title = "Access Secure Data",
                subtitle = "Authenticate to access your data"
            )
            
            when (authResult) {
                BiometricResult.Success -> {
                    // Continue to decryption
                }
                BiometricResult.UserCancelled -> return SecureOperationResult.UserCancelled
                BiometricResult.AuthenticationFailed -> return SecureOperationResult.AuthenticationFailed
                BiometricResult.BiometricNotEnrolled -> return SecureOperationResult.BiometricNotEnrolled
                is BiometricResult.Error -> return SecureOperationResult.Error(authResult.message)
                BiometricResult.BiometricNotAvailable -> {
                    // Continue without biometric auth
                }
            }
        }
        
        // Decrypt the data
        return try {
            val decrypted = dataEncryption.decryptString(encryptedData)
            SecureOperationResult.Success(decrypted)
        } catch (e: Exception) {
            SecureOperationResult.Error("Decryption failed: ${e.message}")
        }
    }
}

/**
 * Result of secure operations combining biometrics and encryption
 */
sealed class SecureOperationResult<out T> {
    data class Success<out T>(val data: T) : SecureOperationResult<T>()
    object UserCancelled : SecureOperationResult<Nothing>()
    object AuthenticationFailed : SecureOperationResult<Nothing>()
    object BiometricNotEnrolled : SecureOperationResult<Nothing>()
    data class Error(val message: String) : SecureOperationResult<Nothing>()
}