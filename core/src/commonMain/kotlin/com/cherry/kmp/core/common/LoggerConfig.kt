package com.cherry.kmp.core.common

import co.touchlab.kermit.Logger
import co.touchlab.kermit.Severity
import co.touchlab.kermit.StaticConfig
import co.touchlab.kermit.platformLogWriter

/**
 * Centralized logging configuration for the application
 * Configures Kermit with default debug settings
 */
object LoggerConfig {
    
    /**
     * Application logger instance with conditional configuration
     */
    val logger: Logger by lazy {
        Logger(
            config = StaticConfig(
                // Default to debug for development, can be overridden
                minSeverity = Severity.Verbose,
                logWriterList = listOf(platformLogWriter())
            ),
            tag = "CherryKMP"
        )
    }
    
    /**
     * Network-specific logger for HTTP requests/responses
     */
    val networkLogger: Logger by lazy {
        Logger(
            config = StaticConfig(
                minSeverity = Severity.Debug,
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
        logger.i { "Core logging initialized" }
    }
}