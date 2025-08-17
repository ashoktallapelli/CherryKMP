package com.cherry.kmp.di

import com.cherry.kmp.data.local.DatabaseMaintenance
import com.cherry.kmp.data.local.SecurityPreferences
import com.cherry.kmp.data.network.DataMallApiService
import com.cherry.kmp.data.repository.SingaporeTravelRepositoryImpl
import com.cherry.kmp.domain.Constants
import com.cherry.kmp.domain.repository.SingaporeTravelRepository
import com.cherry.kmp.domain.usecase.*
import com.cherry.kmp.security.SecurityGuard
import com.cherry.kmp.security.SecurityManager
import com.cherry.kmp.ui.main.viewmodel.MainViewModel
import com.cherry.kmp.ui.main.viewmodel.SingaporeTravelViewModel
import com.cherry.kmp.ui.main.profile.ProfileViewModel
import com.cherry.kmp.ui.settings.SecuritySettingsViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import org.koin.dsl.module

val appModule = module {
    // UseCases can remain singletons as they're stateless
    single { GetPostsUseCase(get(), Dispatchers.IO) }
    single { LocalDataUseCase(get()) }
    single { GetEverythingUseCase(get(), Dispatchers.IO) }
    single { GetTopHeadlinesUseCase(get(), Dispatchers.IO) }
    
    // Singapore Travel UseCases
    single { GetSingaporeTravelInfoUseCase(get(), Dispatchers.IO) }
    single { GetNearbyBusStopsUseCase(get(), Dispatchers.IO) }
    single { GetNearbyMrtStationsUseCase(get(), Dispatchers.IO) }
    single { GetBusArrivalUseCase(get(), Dispatchers.IO) }
    single { GetTrainServiceAlertsUseCase(get(), Dispatchers.IO) }
    single { GetPlatformCrowdDensityUseCase(get(), Dispatchers.IO) }
    
    // Database maintenance
    single { DatabaseMaintenance(get()) }
    
    // Singapore Travel API and Repository
    single { DataMallApiService(get()) }
    single<SingaporeTravelRepository> { SingaporeTravelRepositoryImpl(get(), Constants.DATAMALL_API_KEY) }
    
    // Security components
    single { SecurityPreferences(get(qualifier = org.koin.core.qualifier.named("security_datastore"))) }
    single { SecuritySettingsUseCase(get()) }
    single { SecurityGuard(get(), get()) }
    single { SecurityManager(get(), get()) }
    
    // ViewModels as factories to prevent memory leaks
    // Each screen gets a new instance that dies with the UI
    factory { MainViewModel(get(), get(), get(), get()) }
    factory { ProfileViewModel(get()) }
    factory { SecuritySettingsViewModel(get(), get()) }
    factory { SingaporeTravelViewModel(get(), get(), get(), get(), get(), get()) }
}