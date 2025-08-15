package com.cherry.kmp.security

import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

/**
 * Common interface for data encryption across platforms
 * Platform-specific implementations will handle actual encryption
 */
expect class DataEncryption() {
    
    /**
     * Encrypt sensitive string data
     */
    suspend fun encryptString(plainText: String): String
    
    /**
     * Decrypt previously encrypted string data
     */
    suspend fun decryptString(encryptedText: String): String
    
    /**
     * Generate or retrieve encryption key
     */
    suspend fun getOrCreateKey(): ByteArray
    
    /**
     * Check if encryption is available on this platform
     */
    fun isEncryptionAvailable(): Boolean
}

/**
 * Helper functions for common encryption tasks
 */
object EncryptionHelper {
    
    /**
     * Encrypt JSON serializable data
     */
    suspend inline fun <reified T> encryptData(data: T, encryption: DataEncryption): String? {
        return try {
            if (!encryption.isEncryptionAvailable()) {
                null
            } else {
                val json = Json.encodeToString(data)
                encryption.encryptString(json)
            }
        } catch (e: Exception) {
            null
        }
    }
    
    /**
     * Decrypt JSON data back to object
     */
    suspend inline fun <reified T> decryptData(encryptedData: String, encryption: DataEncryption): T? {
        return try {
            if (!encryption.isEncryptionAvailable()) {
                null
            } else {
                val json = encryption.decryptString(encryptedData)
                Json.decodeFromString<T>(json)
            }
        } catch (e: Exception) {
            null
        }
    }
    
    /**
     * Secure string comparison to prevent timing attacks
     */
    fun secureEquals(a: String, b: String): Boolean {
        if (a.length != b.length) return false
        
        var result = 0
        for (i in a.indices) {
            result = result or (a[i].code xor b[i].code)
        }
        return result == 0
    }
}