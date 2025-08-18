package com.cherry.kmp.datamall

import com.cherry.kmp.datamall.domain.model.*
import com.cherry.kmp.datamall.domain.repository.SingaporeTravelRepository
import com.cherry.kmp.datamall.ui.viewmodel.SingaporeTravelViewModel
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

/**
 * Implementation of DataMall API
 */
internal class DataMallApiImpl : DataMallApi, KoinComponent {
    
    private val repository: SingaporeTravelRepository by inject()
    private val viewModel: SingaporeTravelViewModel by inject()
    
    override fun getSingaporeTravelViewModel(): SingaporeTravelViewModel = viewModel
    
    override suspend fun getTravelInfo(location: LocationData): Result<SingaporeTravelInfo> {
        return repository.getSingaporeTravelInfo(location)
    }
    
    override suspend fun getNearbyBusStops(location: LocationData, radiusKm: Double): Result<List<BusStopWithArrivals>> {
        return repository.getNearbyBusStops(location, radiusKm)
    }
    
    override suspend fun getNearbyMrtStations(location: LocationData, radiusKm: Double): Result<List<MrtStationWithInfo>> {
        return repository.getNearbyMrtStations(location, radiusKm)
    }
    
    override suspend fun getTrainServiceAlerts(): Result<List<TrainAlert>> {
        return repository.getTrainServiceAlerts()
    }
}