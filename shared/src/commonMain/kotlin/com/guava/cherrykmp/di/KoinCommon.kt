package com.guava.cherrykmp.di

import org.koin.core.context.startKoin
import org.koin.dsl.KoinAppDeclaration


fun initKoin(appDeclaration: KoinAppDeclaration = {}) =
    startKoin {
        appDeclaration()
        modules(
            platformModule
        )
    }


//using in iOS
fun initKoin() = initKoin {}