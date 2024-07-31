package com.cherry.kmp.domain.repository

import com.cherry.kmp.data.local.entity.DataModelEntity
import com.cherry.kmp.domain.model.UserProfile
import kotlinx.coroutines.flow.Flow


interface LocalDataRepository {

    //Database
    suspend fun insert(item: DataModelEntity)
    suspend fun insertAll(data: List<DataModelEntity>)
    suspend fun getById(id: Long): DataModelEntity
    suspend fun getAll(): List<DataModelEntity>
    fun getAllAsFlow(): Flow<List<DataModelEntity>>
    suspend fun count(): Int
    suspend fun deleteAll()

    suspend fun insertUserProfile(userProfile: UserProfile)
    suspend fun getUserProfile(userId: Long): UserProfile?
    suspend fun getAllUserProfiles(): List<UserProfile>

    //Data Store
    suspend fun getLanguage(): String?
    suspend fun setLanguage(language: String)
}