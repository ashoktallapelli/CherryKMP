package com.cherry.kmp.di

import com.cherry.kmp.data.local.AppDatabase
import com.cherry.kmp.data.local.SecurityPreferences
import com.cherry.kmp.data.local.getDataStore
import com.cherry.kmp.data.getAppDataStore
import com.cherry.kmp.data.getAppDatabase
import com.cherry.kmp.security.BiometricAuth
import com.cherry.kmp.security.DataEncryption
import org.koin.dsl.module

actual val platformModule = module {
    single<AppDatabase> { getAppDatabase(get()) }
    single { getAppDataStore(get()) }
    
    // Security DataStore (separate from main preferences)  
    single<String>(qualifier = org.koin.core.qualifier.named("security_prefs_path")) { 
        "${get<android.content.Context>().filesDir.absolutePath}/security.preferences_pb" 
    }
    single(qualifier = org.koin.core.qualifier.named("security_datastore")) { 
        getDataStore { get<String>(qualifier = org.koin.core.qualifier.named("security_prefs_path")) } 
    }
    
    // Android Security components
    single { DataEncryption() }
    single { BiometricAuth(get()) }
}