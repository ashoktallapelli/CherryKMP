package com.cherry.kmp.di

import com.cherry.kmp.data.local.AppDatabase
import com.cherry.kmp.data.local.SecurityPreferences
import com.cherry.kmp.data.local.getDataStore
import com.cherry.kmp.data.getAppDataStore
import com.cherry.kmp.data.getAppDatabase
import com.cherry.kmp.security.BiometricAuth
import com.cherry.kmp.security.DataEncryption
import org.koin.dsl.module
import platform.Foundation.NSDocumentDirectory
import platform.Foundation.NSFileManager
import platform.Foundation.NSURL
import platform.Foundation.NSUserDomainMask

actual val platformModule = module {
    single<AppDatabase> { getAppDatabase() }
    single { getAppDataStore() }
    
    // Security DataStore (separate from main preferences)
    single(qualifier = org.koin.core.qualifier.named("security_datastore")) { 
        getDataStore { get<String>(qualifier = org.koin.core.qualifier.named("security_prefs_path")) } 
    }
    single<String>(qualifier = org.koin.core.qualifier.named("security_prefs_path")) { 
        val documentsPath = NSFileManager.defaultManager.URLsForDirectory(
            NSDocumentDirectory,
            NSUserDomainMask
        ).firstOrNull() as? NSURL
        "${documentsPath?.path ?: ""}/security.preferences_pb"
    }
    
    // iOS Security components
    single { DataEncryption() }
    single { BiometricAuth() }
}