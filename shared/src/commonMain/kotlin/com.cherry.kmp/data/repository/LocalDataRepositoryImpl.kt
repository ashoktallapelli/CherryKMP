package com.cherry.kmp.data.repository

import com.cherry.kmp.data.AppDataStore
import com.cherry.kmp.data.AppDatabase
import com.cherry.kmp.data.entity.DataModelEntity
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