package com.cherry.kmp.common

/**
 * Cross-platform time utilities
 */
expect object Clock {
    fun currentTimeMillis(): Long
}