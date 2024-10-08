package com.cherry.kmp.domain.usecase

import com.cherry.kmp.data.local.entity.DataModelEntity
import com.cherry.kmp.domain.model.UserProfile
import com.cherry.kmp.domain.repository.LocalDataRepository
import kotlinx.coroutines.flow.Flow

class LocalDataUseCase(private val repository: LocalDataRepository) {

    fun getAllAsFlow(): Flow<List<DataModelEntity>> {
        return repository.getAllAsFlow()
    }

    suspend fun insert(item: DataModelEntity) {
        repository.insert(item)
    }

    suspend fun getById(id: Long): DataModelEntity {
        return repository.getById(id)
    }

    suspend fun deleteAll() {
        repository.deleteAll()
    }

    suspend fun saveUserProfile(userProfile: UserProfile) {
        repository.insertUserProfile(userProfile)
    }

    suspend fun getUserProfile(userId: Long): UserProfile? {
        return repository.getUserProfile(userId)
    }

    suspend fun getAllUserProfiles(): List<UserProfile> {
        return repository.getAllUserProfiles()
    }
}