package com.cherry.kmp.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.cherry.kmp.data.entity.DataModelEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface DataDao {
    @Insert
    suspend fun insert(item: DataModelEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(data: List<DataModelEntity>)

    @Query("DELETE FROM data_table")
    suspend fun deleteAll()

    @Query("SELECT count(*) FROM data_table")
    suspend fun count(): Int

    @Query("SELECT * FROM data_table WHERE id = :id")
    suspend fun getById(id: Long): DataModelEntity

    @Query("SELECT * FROM data_table")
    fun getAllAsFlow(): Flow<List<DataModelEntity>>
}