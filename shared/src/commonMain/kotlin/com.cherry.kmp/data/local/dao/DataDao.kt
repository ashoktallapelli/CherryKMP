package com.cherry.kmp.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.cherry.kmp.data.local.entity.DataModelEntity
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

    @Query("SELECT * FROM data_table WHERE id = :id LIMIT 1")
    suspend fun getById(id: Long): DataModelEntity?

    @Query("SELECT * FROM data_table ORDER BY name ASC")
    suspend fun getAll(): List<DataModelEntity>

    @Query("SELECT * FROM data_table ORDER BY name ASC")
    fun getAllAsFlow(): Flow<List<DataModelEntity>>

    @Query("SELECT * FROM data_table WHERE name LIKE '%' || :searchTerm || '%' ORDER BY name ASC")
    suspend fun searchByName(searchTerm: String): List<DataModelEntity>

    @Query("DELETE FROM data_table WHERE id = :id")
    suspend fun deleteById(id: Long): Int
}