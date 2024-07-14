package com.cherry.kmp.di

import org.koin.core.context.startKoin
import org.koin.dsl.KoinAppDeclaration


fun initKoin(appDeclaration: KoinAppDeclaration = {}) =
    startKoin {
        appDeclaration()
        modules(
            appModule, networkModule, platformModule
        )
    }


//using in iOS
fun initKoin() = initKoin {}