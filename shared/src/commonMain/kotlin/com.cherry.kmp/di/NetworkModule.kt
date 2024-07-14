package com.cherry.kmp.di

import com.cherry.kmp.data.local.AppDataStore
import com.cherry.kmp.domain.repository.LocalDataRepository
import com.cherry.kmp.data.repository.LocalDataRepositoryImpl
import com.cherry.kmp.data.network.ApiService
import com.cherry.kmp.data.network.httpClient
import com.cherry.kmp.data.repository.RepositoryImpl
import com.cherry.kmp.domain.repository.Repository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import org.koin.dsl.module

val networkModule = module {
    single { httpClient }
    single { ApiService(get()) }
    single { AppDataStore(get()) }
    single<LocalDataRepository> { LocalDataRepositoryImpl(get(), get(), Dispatchers.IO) }
    single<Repository> { RepositoryImpl(get()) }
}
