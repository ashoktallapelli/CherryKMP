package com.guava.cherrykmp.di

import com.guava.cherrykmp.data.AppDatabase
import com.guava.cherrykmp.data.getAppDatabase
import org.koin.dsl.module

actual val platformModule = module {
    single<AppDatabase> {
        getAppDatabase(get())
    }
}