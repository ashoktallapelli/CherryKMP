package com.cherry.kmp.domain.usecase

import com.cherry.kmp.domain.model.*
import com.cherry.kmp.domain.repository.SingaporeTravelRepository
import com.cherry.kmp.domain.usecase.base.UseCase
import kotlinx.coroutines.CoroutineDispatcher

/**
 * Use case for getting comprehensive Singapore travel information
 */
class GetSingaporeTravelInfoUseCase(
    private val repository: SingaporeTravelRepository,
    dispatcher: CoroutineDispatcher
) : UseCase<LocationData, SingaporeTravelInfo>(dispatcher) {
    
    override suspend fun execute(params: LocationData): SingaporeTravelInfo {
        val result = repository.getSingaporeTravelInfo(params)
        return result.getOrElse { 
            // Return empty data if there's an error, letting the UI handle the state
            throw it
        }
    }
}

/**
 * Use case for getting bus arrival information
 */
class GetBusArrivalUseCase(
    private val repository: SingaporeTravelRepository,
    dispatcher: CoroutineDispatcher
) : UseCase<BusArrivalParams, BusArrivalResponse>(dispatcher) {
    
    override suspend fun execute(params: BusArrivalParams): BusArrivalResponse {
        val result = repository.getBusArrival(params.busStopCode, params.serviceNo)
        return result.getOrElse { throw it }
    }
}

data class BusArrivalParams(
    val busStopCode: String,
    val serviceNo: String? = null
)

/**
 * Use case for getting nearby bus stops with arrivals
 */
class GetNearbyBusStopsUseCase(
    private val repository: SingaporeTravelRepository,
    dispatcher: CoroutineDispatcher
) : UseCase<NearbyTransportParams, List<BusStopWithArrivals>>(dispatcher) {
    
    override suspend fun execute(params: NearbyTransportParams): List<BusStopWithArrivals> {
        val result = repository.getNearbyBusStops(params.location, params.radiusKm)
        return result.getOrElse { throw it }
    }
}

/**
 * Use case for getting nearby MRT stations
 */
class GetNearbyMrtStationsUseCase(
    private val repository: SingaporeTravelRepository,
    dispatcher: CoroutineDispatcher
) : UseCase<NearbyTransportParams, List<MrtStationWithInfo>>(dispatcher) {
    
    override suspend fun execute(params: NearbyTransportParams): List<MrtStationWithInfo> {
        val result = repository.getNearbyMrtStations(params.location, params.radiusKm)
        return result.getOrElse { throw it }
    }
}

data class NearbyTransportParams(
    val location: LocationData,
    val radiusKm: Double = 1.0
)

/**
 * Use case for getting train service alerts
 */
class GetTrainServiceAlertsUseCase(
    private val repository: SingaporeTravelRepository,
    dispatcher: CoroutineDispatcher
) : UseCase<Unit, List<TrainAlert>>(dispatcher) {
    
    override suspend fun execute(params: Unit): List<TrainAlert> {
        val result = repository.getTrainServiceAlerts()
        return result.getOrElse { throw it }
    }
}

/**
 * Use case for getting platform crowd density
 */
class GetPlatformCrowdDensityUseCase(
    private val repository: SingaporeTravelRepository,
    dispatcher: CoroutineDispatcher
) : UseCase<String?, List<PlatformCrowdDensity>>(dispatcher) {
    
    override suspend fun execute(params: String?): List<PlatformCrowdDensity> {
        val result = repository.getPlatformCrowdDensity(params)
        return result.getOrElse { throw it }
    }
}