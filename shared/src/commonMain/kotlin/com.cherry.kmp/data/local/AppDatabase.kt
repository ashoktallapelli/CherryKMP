package com.cherry.kmp.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.cherry.kmp.data.local.dao.DataDao
import com.cherry.kmp.data.local.dao.UserProfileDao
import com.cherry.kmp.data.local.entity.DataModelEntity
import com.cherry.kmp.data.local.entity.UserProfileEntity

internal const val dbFileName = "guava.db"

@Database(entities = [DataModelEntity::class, UserProfileEntity::class], version = 1)
abstract class AppDatabase : RoomDatabase(), DB {
    abstract fun dataDao(): DataDao
    abstract fun userProfileDao(): UserProfileDao

    override fun clearAllTables() {
        super.clearAllTables()
    }
}

//class LocalDateTimeConverter {
//    @TypeConverter
//    fun fromTimestamp(value: String?): LocalDateTime? {
//        return value?.let { LocalDateTime.parse(it) }
//    }
//
//    @TypeConverter
//    fun dateToTimestamp(date: LocalDateTime?): String? {
//        return date?.toString()
//    }
//}


// FIXME: Added a hack to resolve below issue:
// Class 'AppDatabase_Impl' is not abstract and does not implement abstract base class member 'clearAllTables'.
interface DB {
    fun clearAllTables(): Unit {}
}