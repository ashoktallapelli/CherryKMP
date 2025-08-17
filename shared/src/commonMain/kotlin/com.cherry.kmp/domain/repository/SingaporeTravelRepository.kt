package com.cherry.kmp.domain.repository

import com.cherry.kmp.domain.model.*

interface SingaporeTravelRepository {
    suspend fun getBusArrival(busStopCode: String, serviceNo: String? = null): Result<BusArrivalResponse>
    suspend fun getTrainServiceAlerts(): Result<List<TrainAlert>>
    suspend fun getPlatformCrowdDensity(trainLine: String? = null): Result<List<PlatformCrowdDensity>>
    suspend fun getBusStops(skip: Int = 0): Result<List<BusStop>>
    suspend fun getTrainStations(skip: Int = 0): Result<List<MrtStation>>
    suspend fun getNearbyBusStops(location: LocationData, radiusKm: Double = 1.0): Result<List<BusStopWithArrivals>>
    suspend fun getNearbyMrtStations(location: LocationData, radiusKm: Double = 1.0): Result<List<MrtStationWithInfo>>
    suspend fun getSingaporeTravelInfo(location: LocationData): Result<SingaporeTravelInfo>
}