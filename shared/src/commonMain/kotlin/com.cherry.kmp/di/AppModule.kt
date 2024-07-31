package com.cherry.kmp.di

import com.cherry.kmp.domain.usecase.GetEverythingUseCase
import com.cherry.kmp.domain.usecase.GetPostsUseCase
import com.cherry.kmp.domain.usecase.GetTopHeadlinesUseCase
import com.cherry.kmp.domain.usecase.LocalDataUseCase
import com.cherry.kmp.ui.main.viewmodel.MainViewModel
import com.cherry.kmp.ui.main.viewmodel.ProfileViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import org.koin.dsl.module

val appModule = module {
    single { GetPostsUseCase(get(), Dispatchers.IO) }
    single { LocalDataUseCase(get()) }
    single { GetEverythingUseCase(get(), Dispatchers.IO) }
    single { GetTopHeadlinesUseCase(get(), Dispatchers.IO) }
    single { MainViewModel(get(), get(), get(), get()) }
    single { ProfileViewModel(get()) }
}