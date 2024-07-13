package com.guava.cherrykmp.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.guava.cherrykmp.data.entity.DataModelEntity

@Dao
interface DataDao {
    @Query("SELECT * FROM data_table")
    suspend fun getAllData(): List<DataModelEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(data: List<DataModelEntity>)
}