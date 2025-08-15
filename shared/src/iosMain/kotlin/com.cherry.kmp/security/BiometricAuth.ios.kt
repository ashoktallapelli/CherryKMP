package com.cherry.kmp.security

import kotlinx.coroutines.suspendCancellableCoroutine
import platform.Foundation.NSError
import platform.LocalAuthentication.LAContext
import platform.LocalAuthentication.LAErrorAuthenticationFailed
import platform.LocalAuthentication.LAErrorBiometryNotAvailable
import platform.LocalAuthentication.LAErrorBiometryNotEnrolled
import platform.LocalAuthentication.LAErrorUserCancel
import platform.LocalAuthentication.LAPolicyDeviceOwnerAuthenticationWithBiometrics
import kotlin.coroutines.resume

/**
 * iOS implementation of biometric authentication using LocalAuthentication framework
 */
actual class BiometricAuth {
    
    private val context = LAContext()
    
    actual suspend fun isBiometricAvailable(): Boolean {
        var error: NSError? = null
        val result = context.canEvaluatePolicy(
            LAPolicyDeviceOwnerAuthenticationWithBiometrics, 
            error = null
        )
        return result
    }
    
    actual suspend fun hasBiometricEnrolled(): Boolean {
        return isBiometricAvailable() // On iOS, canEvaluatePolicy checks enrollment too
    }
    
    actual suspend fun authenticate(
        title: String,
        subtitle: String,
        negativeButtonText: String
    ): BiometricResult = suspendCancellableCoroutine { continuation ->
        
        val reason = "$title\n$subtitle"
        
        context.evaluatePolicy(
            policy = LAPolicyDeviceOwnerAuthenticationWithBiometrics,
            localizedReason = reason
        ) { success, error ->
            val result = if (success) {
                BiometricResult.Success
            } else {
                when (error?.code) {
                    LAErrorUserCancel -> BiometricResult.UserCancelled
                    LAErrorAuthenticationFailed -> BiometricResult.AuthenticationFailed
                    LAErrorBiometryNotAvailable -> BiometricResult.BiometricNotAvailable
                    LAErrorBiometryNotEnrolled -> BiometricResult.BiometricNotEnrolled
                    else -> BiometricResult.Error(
                        error?.localizedDescription ?: "Unknown biometric error"
                    )
                }
            }
            continuation.resume(result)
        }
        
        continuation.invokeOnCancellation {
            // iOS LocalAuthentication doesn't provide cancellation API
            // The operation will complete naturally
        }
    }
}