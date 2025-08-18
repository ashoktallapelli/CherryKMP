package com.cherry.kmp.core.security

/**
 * Security configuration for the application
 */
object SecurityConfig {

    // Certificate pinning configuration for newsapi.org
    // These are the SHA-256 hashes of the certificate's public key
    val PINNED_CERTIFICATES = mapOf(
        "newsapi.org" to listOf(
            "sha256/AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA=", // Primary cert
            "sha256/BBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBBB="  // Backup cert
        )
    )

    // Security headers to validate
    val REQUIRED_SECURITY_HEADERS = listOf(
        "strict-transport-security",
        "x-content-type-options",
        "x-frame-options"
    )

    // Encryption settings
    const val ENCRYPTION_ALGORITHM = "AES/GCM/NoPadding"
    const val KEY_SIZE = 256
    const val IV_SIZE = 12
    const val TAG_SIZE = 16
}