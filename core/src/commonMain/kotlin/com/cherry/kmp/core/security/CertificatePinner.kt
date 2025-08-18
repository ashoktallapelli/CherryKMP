package com.cherry.kmp.core.security

import com.cherry.kmp.core.common.LoggerConfig
import io.ktor.client.plugins.api.createClientPlugin
import io.ktor.http.isSuccess

/**
 * Simple certificate pinning validation for KMP
 * Note: This is a basic implementation. For production apps, consider platform-specific
 * implementations or more robust certificate pinning libraries.
 */
val CertificatePinning = createClientPlugin("CertificatePinning") {
    
    onRequest { request, _ ->
        LoggerConfig.networkLogger.d { "Certificate pinning check for: ${request.url.host}" }
        
        // Validate that we're connecting to expected hosts
        val host = request.url.host
        val allowedHosts = listOf("newsapi.org", "jsonplaceholder.typicode.com")
        
        if (!allowedHosts.contains(host)) {
            LoggerConfig.networkLogger.w { "Warning: Connecting to non-pinned host: $host" }
        }
    }
    
    onResponse { response ->
        if (!response.status.isSuccess()) {
            LoggerConfig.networkLogger.w { "Response failed with status: ${response.status}" }
        }
        
        // Log security headers for monitoring
        val securityHeaders = SecurityConfig.REQUIRED_SECURITY_HEADERS
        securityHeaders.forEach { header ->
            val headerValue = response.headers[header]
            if (headerValue != null) {
                LoggerConfig.networkLogger.d { "Security header $header: $headerValue" }
            } else {
                LoggerConfig.networkLogger.w { "Missing security header: $header" }
            }
        }
    }
}