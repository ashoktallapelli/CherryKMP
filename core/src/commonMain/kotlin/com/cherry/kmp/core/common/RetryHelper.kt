package com.cherry.kmp.core.common

import com.cherry.kmp.core.domain.exception.ApiException
import kotlinx.coroutines.delay

/**
 * Simple retry helper for network operations
 */
object RetryHelper {
    
    /**
     * Retry operation up to 3 times with simple delay
     */
    suspend fun <T> retry(operation: suspend () -> T): T {
        var lastException: Exception? = null
        
        repeat(3) { attempt ->
            try {
                return operation()
            } catch (e: Exception) {
                lastException = e
                
                // Don't retry client errors (4xx)
                if (e is ApiException.BadRequestException || 
                    e is ApiException.UnauthorizedException || 
                    e is ApiException.NotFoundException) {
                    throw e
                }
                
                // Delay before retry (except last attempt)
                if (attempt < 2) {
                    delay(1000L * (attempt + 1)) // 1s, 2s delay
                }
            }
        }
        
        throw lastException ?: Exception("Retry failed")
    }
}