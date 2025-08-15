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

    @Query("SELECT * FROM user_profile WHERE id = :userId LIMIT 1")
    suspend fun get(userId: Long): UserProfileEntity?

    @Query("SELECT * FROM user_profile WHERE email = :email LIMIT 1")
    suspend fun getByEmail(email: String): UserProfileEntity?

    @Query("SELECT * FROM user_profile ORDER BY name ASC")
    suspend fun getAll(): List<UserProfileEntity>

    @Query("SELECT COUNT(*) FROM user_profile")
    suspend fun count(): Int

    @Query("DELETE FROM user_profile WHERE id = :userId")
    suspend fun deleteById(userId: Long): Int
}