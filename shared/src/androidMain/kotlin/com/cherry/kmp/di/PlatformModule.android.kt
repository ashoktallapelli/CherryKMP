package com.cherry.kmp.di

import com.cherry.kmp.data.AppDatabase
import com.cherry.kmp.data.getAppDatabase
import org.koin.dsl.module

actual val platformModule = module {
    single<AppDatabase> {
        getAppDatabase(get())
    }
}