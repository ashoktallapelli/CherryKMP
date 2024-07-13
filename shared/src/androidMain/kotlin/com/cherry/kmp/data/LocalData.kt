package com.cherry.kmp.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.room.Room
import androidx.sqlite.driver.bundled.BundledSQLiteDriver

fun getAppDatabase(context: Context): AppDatabase {
    val dbFile = context.getDatabasePath(dbFileName)
    return Room.databaseBuilder<AppDatabase>(
        context = context.applicationContext, name = dbFile.absolutePath
    ).setDriver(BundledSQLiteDriver()).fallbackToDestructiveMigration(true).build()
}

fun getAppDataStore(context: Context): DataStore<Preferences> = getDataStore(
    producePath = { context.filesDir.resolve(dataStoreFileName).absolutePath }
)