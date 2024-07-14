package com.cherry.kmp.di

import com.cherry.kmp.data.local.AppDatabase
import com.cherry.kmp.data.getAppDataStore
import com.cherry.kmp.data.getAppDatabase
import org.koin.dsl.module

actual val platformModule = module {
    single<AppDatabase> { getAppDatabase() }
    single { getAppDataStore() }
}