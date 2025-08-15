package com.cherry.kmp.di

import com.cherry.kmp.data.local.DatabaseMaintenance
import com.cherry.kmp.domain.usecase.GetEverythingUseCase
import com.cherry.kmp.domain.usecase.GetPostsUseCase
import com.cherry.kmp.domain.usecase.GetTopHeadlinesUseCase
import com.cherry.kmp.domain.usecase.LocalDataUseCase
import com.cherry.kmp.ui.main.viewmodel.MainViewModel
import com.cherry.kmp.ui.main.profile.ProfileViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import org.koin.dsl.module

val appModule = module {
    // UseCases can remain singletons as they're stateless
    single { GetPostsUseCase(get(), Dispatchers.IO) }
    single { LocalDataUseCase(get()) }
    single { GetEverythingUseCase(get(), Dispatchers.IO) }
    single { GetTopHeadlinesUseCase(get(), Dispatchers.IO) }
    
    // Database maintenance
    single { DatabaseMaintenance(get()) }
    
    // ViewModels as factories to prevent memory leaks
    // Each screen gets a new instance that dies with the UI
    factory { MainViewModel(get(), get(), get(), get()) }
    factory { ProfileViewModel(get()) }
}