package com.cherry.kmp.common

import CherryKMP.shared.BuildConfig
import co.touchlab.kermit.Logger
import co.touchlab.kermit.Severity
import co.touchlab.kermit.StaticConfig
import co.touchlab.kermit.platformLogWriter

/**
 * Centralized logging configuration for the application
 * Configures Kermit based on BuildConfig.DEBUG flag
 */
object LoggerConfig {
    
    /**
     * Application logger instance with conditional configuration
     */
    val logger: Logger by lazy {
        Logger(
            config = StaticConfig(
                // Only log in debug builds, disable in release
                minSeverity = if (BuildConfig.DEBUG) Severity.Verbose else Severity.Error,
                logWriterList = listOf(platformLogWriter())
            ),
            tag = "CherryKMP"
        )
    }
    
    /**
     * Network-specific logger for HTTP requests/responses
     * Only logs in debug builds to prevent API key leakage
     */
    val networkLogger: Logger by lazy {
        Logger(
            config = StaticConfig(
                minSeverity = if (BuildConfig.DEBUG) Severity.Debug else Severity.Assert,
                logWriterList = listOf(platformLogWriter())
            ),
            tag = "Network"
        )
    }
    
    /**
     * Initialize logging configuration
     * Call this once at app startup
     */
    fun initialize() {
        if (BuildConfig.DEBUG) {
            logger.i { "Logging initialized - DEBUG mode enabled" }
        }
    }
}