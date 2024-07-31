package com.cherry.kmp.data.repository

import com.cherry.kmp.data.local.AppDataStore
import com.cherry.kmp.data.local.AppDatabase
import com.cherry.kmp.data.local.entity.DataModelEntity
import com.cherry.kmp.data.local.entity.asEntity
import com.cherry.kmp.data.local.entity.asExternalModel
import com.cherry.kmp.domain.model.UserProfile
import com.cherry.kmp.domain.repository.LocalDataRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext

class LocalDataRepositoryImpl(
    private val db: AppDatabase,
    private val dStore: AppDataStore,
    private val dispatcher: CoroutineDispatcher
) : LocalDataRepository {

    override suspend fun insert(item: DataModelEntity) {
        withContext(dispatcher) {
            db.dataDao().insert(item)
        }
    }

    override suspend fun insertAll(data: List<DataModelEntity>) {
        withContext(dispatcher) {
            db.dataDao().insertAll(data)
        }
    }

    override suspend fun getById(id: Long): DataModelEntity {
        return withContext(dispatcher) {
            db.dataDao().getById(id)
        }
    }

    override suspend fun getAll(): List<DataModelEntity> {
        return withContext(dispatcher) {
            db.dataDao().getAll()
        }
    }

    override fun getAllAsFlow(): Flow<List<DataModelEntity>> {
        return db.dataDao().getAllAsFlow()
    }

    override suspend fun insertUserProfile(userProfile: UserProfile) {
        withContext(dispatcher) {
            db.userProfileDao().save(userProfile.asEntity())
        }
    }

    override suspend fun getAllUserProfiles(): List<UserProfile> {
        return withContext(dispatcher) {
            db.userProfileDao().getAll().map {
                it.asExternalModel()
            }
        }
    }

    override suspend fun getUserProfile(userId: Long): UserProfile? {
        return withContext(dispatcher) {
            db.userProfileDao().get(userId = userId)?.asExternalModel()
        }
    }

    override suspend fun count(): Int {
        return withContext(dispatcher) {
            db.dataDao().count()
        }
    }

    override suspend fun deleteAll() {
        withContext(dispatcher) {
            db.dataDao().deleteAll()
        }
    }

    override suspend fun getLanguage(): String? {
        return withContext(dispatcher) {
            dStore.language.first()
        }
    }

    override suspend fun setLanguage(language: String) {
        withContext(dispatcher) {
            dStore.saveLanguage(language)
        }
    }
}