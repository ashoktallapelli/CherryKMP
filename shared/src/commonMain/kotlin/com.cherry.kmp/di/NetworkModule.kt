package com.cherry.kmp.di

import com.cherry.kmp.data.network.ApiService
import com.cherry.kmp.data.network.httpClient
import com.cherry.kmp.data.repository.RepositoryImpl
import com.cherry.kmp.domain.repository.Repository
import org.koin.dsl.module

val networkModule = module {
    single { httpClient }
    single { ApiService(get()) }
    single<Repository> { RepositoryImpl(get()) }
}
