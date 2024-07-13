package com.guava.cherrykmp.android

import android.app.Application
import com.guava.cherrykmp.di.initKoin
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger


class MyApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        instance = this
        initKoin {
            androidLogger()
            androidContext(this@MyApplication)
        }
    }

    companion object {
        lateinit var instance: MyApplication
            private set
    }
}