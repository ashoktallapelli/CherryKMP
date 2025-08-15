package com.cherry.kmp.security


/**
 * iOS implementation using iOS Security framework
 * Note: This is a simplified implementation. Production apps should use
 * iOS Keychain services for more robust encryption.
 */
actual class DataEncryption {
    
    actual suspend fun encryptString(plainText: String): String {
        // Simplified XOR encryption for demo purposes
        // In production, use CryptoKit or CommonCrypto
        val key = getOrCreateKey()
        val data = plainText.encodeToByteArray()
        val encrypted = data.mapIndexed { index, byte -> 
            (byte.toInt() xor key[index % key.size].toInt()).toByte() 
        }.toByteArray()
        
        return encrypted.joinToString(",") { it.toString() }
    }
    
    actual suspend fun decryptString(encryptedText: String): String {
        val key = getOrCreateKey()
        val data = encryptedText.split(",").map { it.toByte() }.toByteArray()
        val decrypted = data.mapIndexed { index, byte -> 
            (byte.toInt() xor key[index % key.size].toInt()).toByte() 
        }.toByteArray()
        
        return decrypted.decodeToString()
    }
    
    actual suspend fun getOrCreateKey(): ByteArray {
        // Generate a simple key for demo
        // In production, store this in iOS Keychain
        return "CherryKMP_Secret_Key_32_Bytes_Long".encodeToByteArray().take(32).toByteArray()
    }
    
    actual fun isEncryptionAvailable(): Boolean {
        return true // Always available on iOS
    }
    
    /**
     * Generate secure random bytes using iOS Security framework
     * Simplified version for demo purposes
     */
    private fun generateSecureRandomBytes(size: Int): ByteArray {
        // Simplified random generation for demo
        return ByteArray(size) { (kotlin.random.Random.nextDouble() * 256).toInt().toByte() }
    }
}