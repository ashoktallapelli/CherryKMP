package com.cherry.kmp.security

import android.content.Context
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

/**
 * Android implementation of biometric authentication using AndroidX Biometric
 */
actual class BiometricAuth(
    private val context: Context,
    private val activity: FragmentActivity? = null
) {
    
    actual suspend fun isBiometricAvailable(): Boolean {
        val biometricManager = BiometricManager.from(context)
        return when (biometricManager.canAuthenticate(BiometricManager.Authenticators.BIOMETRIC_STRONG)) {
            BiometricManager.BIOMETRIC_SUCCESS -> true
            else -> false
        }
    }
    
    actual suspend fun hasBiometricEnrolled(): Boolean {
        val biometricManager = BiometricManager.from(context)
        return when (biometricManager.canAuthenticate(BiometricManager.Authenticators.BIOMETRIC_STRONG)) {
            BiometricManager.BIOMETRIC_SUCCESS -> true
            BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED -> false
            else -> false
        }
    }
    
    actual suspend fun authenticate(
        title: String,
        subtitle: String,
        negativeButtonText: String
    ): BiometricResult = suspendCancellableCoroutine { continuation ->
        
        val currentActivity = activity
        if (currentActivity == null) {
            continuation.resume(BiometricResult.Error("Activity not available"))
            return@suspendCancellableCoroutine
        }
        
        val executor = ContextCompat.getMainExecutor(context)
        val biometricPrompt = BiometricPrompt(currentActivity, executor,
            object : BiometricPrompt.AuthenticationCallback() {
                override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                    super.onAuthenticationError(errorCode, errString)
                    val result = when (errorCode) {
                        BiometricPrompt.ERROR_USER_CANCELED -> BiometricResult.UserCancelled
                        BiometricPrompt.ERROR_NEGATIVE_BUTTON -> BiometricResult.UserCancelled
                        BiometricPrompt.ERROR_NO_BIOMETRICS -> BiometricResult.BiometricNotEnrolled
                        BiometricPrompt.ERROR_HW_NOT_PRESENT -> BiometricResult.BiometricNotAvailable
                        BiometricPrompt.ERROR_HW_UNAVAILABLE -> BiometricResult.BiometricNotAvailable
                        else -> BiometricResult.Error(errString.toString())
                    }
                    continuation.resume(result)
                }
                
                override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                    super.onAuthenticationSucceeded(result)
                    continuation.resume(BiometricResult.Success)
                }
                
                override fun onAuthenticationFailed() {
                    super.onAuthenticationFailed()
                    continuation.resume(BiometricResult.AuthenticationFailed)
                }
            }
        )
        
        val promptInfo = BiometricPrompt.PromptInfo.Builder()
            .setTitle(title)
            .setSubtitle(subtitle)
            .setNegativeButtonText(negativeButtonText)
            .setAllowedAuthenticators(BiometricManager.Authenticators.BIOMETRIC_STRONG)
            .build()
        
        try {
            biometricPrompt.authenticate(promptInfo)
        } catch (e: Exception) {
            continuation.resume(BiometricResult.Error(e.message ?: "Authentication failed"))
        }
        
        continuation.invokeOnCancellation {
            try {
                biometricPrompt.cancelAuthentication()
            } catch (_: Exception) {
                // Ignore cancellation errors
            }
        }
    }
}