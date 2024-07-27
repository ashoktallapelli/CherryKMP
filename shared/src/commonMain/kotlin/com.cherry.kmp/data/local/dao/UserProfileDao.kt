package com.cherry.kmp.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.cherry.kmp.data.local.entity.UserProfileEntity


@Dao
interface UserProfileDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun save(userProfile: UserProfileEntity)

    @Query("SELECT * FROM user_profile WHERE id = :userId")
    suspend fun get(userId: Long): UserProfileEntity?

    @Query("SELECT * FROM user_profile")
    suspend fun getAll(): List<UserProfileEntity>
}