package com.cherry.kmp.di

import org.koin.core.context.startKoin
import org.koin.dsl.KoinAppDeclaration
import com.cherry.kmp.datamall.di.dataMallModule


fun initKoin(appDeclaration: KoinAppDeclaration = {}) =
    startKoin {
        appDeclaration()
        modules(
            appModule, networkModule, platformModule, dataMallModule
        )
    }


//using in iOS
fun initKoin() = initKoin {}