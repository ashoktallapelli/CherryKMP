package com.cherry.kmp.datamall

import com.cherry.kmp.datamall.domain.model.*
import com.cherry.kmp.datamall.ui.viewmodel.SingaporeTravelViewModel

/**
 * Public API interface for DataMall module
 * This is the main entry point for consuming the DataMall functionality
 */
interface DataMallApi {
    /**
     * Get the Singapore Travel ViewModel
     */
    fun getSingaporeTravelViewModel(): SingaporeTravelViewModel
    
    /**
     * Get travel information for a specific location
     */
    suspend fun getTravelInfo(location: LocationData): Result<SingaporeTravelInfo>
    
    /**
     * Get nearby bus stops with arrival information
     */
    suspend fun getNearbyBusStops(location: LocationData, radiusKm: Double = 1.0): Result<List<BusStopWithArrivals>>
    
    /**
     * Get nearby MRT stations with crowd information
     */
    suspend fun getNearbyMrtStations(location: LocationData, radiusKm: Double = 1.0): Result<List<MrtStationWithInfo>>
    
    /**
     * Get train service alerts
     */
    suspend fun getTrainServiceAlerts(): Result<List<TrainAlert>>
}

/**
 * Factory for creating DataMall API instances
 */
object DataMallApiFactory {
    fun create(): DataMallApi = DataMallApiImpl()
}