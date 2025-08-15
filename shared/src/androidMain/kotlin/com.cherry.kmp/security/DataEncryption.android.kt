package com.cherry.kmp.security

import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import android.util.Base64
import java.security.KeyStore
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.GCMParameterSpec

/**
 * Android implementation using Android Keystore for hardware-backed encryption
 */
actual class DataEncryption {
    
    private val keyAlias = "CherryKMP_DataEncryption_Key"
    private val keyStore = KeyStore.getInstance("AndroidKeyStore")
    
    init {
        keyStore.load(null)
    }
    
    actual suspend fun encryptString(plainText: String): String {
        val secretKey = getOrCreateSecretKey()
        val cipher = Cipher.getInstance(SecurityConfig.ENCRYPTION_ALGORITHM)
        cipher.init(Cipher.ENCRYPT_MODE, secretKey)
        
        val iv = cipher.iv
        val encryptedData = cipher.doFinal(plainText.toByteArray())
        
        // Combine IV + encrypted data
        val combined = iv + encryptedData
        return Base64.encodeToString(combined, Base64.NO_WRAP)
    }
    
    actual suspend fun decryptString(encryptedText: String): String {
        val secretKey = getOrCreateSecretKey()
        val combined = Base64.decode(encryptedText, Base64.NO_WRAP)
        
        // Extract IV and encrypted data
        val iv = combined.sliceArray(0..SecurityConfig.IV_SIZE - 1)
        val encryptedData = combined.sliceArray(SecurityConfig.IV_SIZE until combined.size)
        
        val cipher = Cipher.getInstance(SecurityConfig.ENCRYPTION_ALGORITHM)
        val spec = GCMParameterSpec(SecurityConfig.TAG_SIZE * 8, iv)
        cipher.init(Cipher.DECRYPT_MODE, secretKey, spec)
        
        val decryptedData = cipher.doFinal(encryptedData)
        return String(decryptedData)
    }
    
    actual suspend fun getOrCreateKey(): ByteArray {
        val secretKey = getOrCreateSecretKey()
        return secretKey.encoded
    }
    
    actual fun isEncryptionAvailable(): Boolean {
        return android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M
    }
    
    private fun getOrCreateSecretKey(): SecretKey {
        return if (keyStore.containsAlias(keyAlias)) {
            keyStore.getKey(keyAlias, null) as SecretKey
        } else {
            generateSecretKey()
        }
    }
    
    private fun generateSecretKey(): SecretKey {
        val keyGenerator = KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES, "AndroidKeyStore")
        val keyGenParameterSpec = KeyGenParameterSpec.Builder(
            keyAlias,
            KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT
        )
            .setBlockModes(KeyProperties.BLOCK_MODE_GCM)
            .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
            .setKeySize(SecurityConfig.KEY_SIZE)
            .setUserAuthenticationRequired(false) // Set to true for biometric requirement
            .build()
        
        keyGenerator.init(keyGenParameterSpec)
        return keyGenerator.generateKey()
    }
}