package com.cherry.kmp.di

import com.cherry.kmp.domain.usecase.GetPostsUseCase
import com.cherry.kmp.domain.usecase.GetQuotesUseCase
import com.cherry.kmp.ui.main.MainViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import org.koin.dsl.module

val appModule = module {
    single { MainViewModel(get()) }
    single { GetPostsUseCase(get(), Dispatchers.IO) }
    single { GetQuotesUseCase(get(), Dispatchers.IO) }
}