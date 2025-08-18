package com.cherry.kmp.datamall.di

import com.cherry.kmp.datamall.data.network.DataMallApiService
import com.cherry.kmp.datamall.data.repository.SingaporeTravelRepositoryImpl
import com.cherry.kmp.datamall.domain.repository.SingaporeTravelRepository
import com.cherry.kmp.datamall.domain.usecase.*
import com.cherry.kmp.datamall.ui.viewmodel.SingaporeTravelViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import org.koin.dsl.module
import CherryKMP.datamall.BuildConfig

val dataMallModule = module {
    // Singapore Travel API and Repository
    single { DataMallApiService(get()) }
    single<SingaporeTravelRepository> { 
        SingaporeTravelRepositoryImpl(get(), BuildConfig.DATAMALL_API_KEY)
    }
    
    // Singapore Travel UseCases
    single { GetSingaporeTravelInfoUseCase(get(), Dispatchers.IO) }
    single { GetNearbyBusStopsUseCase(get(), Dispatchers.IO) }
    single { GetNearbyMrtStationsUseCase(get(), Dispatchers.IO) }
    single { GetBusArrivalUseCase(get(), Dispatchers.IO) }
    single { GetTrainServiceAlertsUseCase(get(), Dispatchers.IO) }
    single { GetPlatformCrowdDensityUseCase(get(), Dispatchers.IO) }
    
    // ViewModels as factories to prevent memory leaks
    factory { SingaporeTravelViewModel(get(), get(), get(), get(), get(), get()) }
}