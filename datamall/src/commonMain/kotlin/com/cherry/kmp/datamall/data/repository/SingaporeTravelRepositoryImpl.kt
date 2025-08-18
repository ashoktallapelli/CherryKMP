package com.cherry.kmp.datamall.data.repository

import com.cherry.kmp.core.common.RetryHelper
import com.cherry.kmp.datamall.data.network.DataMallApiService
import com.cherry.kmp.datamall.data.network.*
import com.cherry.kmp.datamall.domain.model.*
import com.cherry.kmp.datamall.domain.repository.SingaporeTravelRepository
import io.ktor.client.call.body
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlin.math.*

class SingaporeTravelRepositoryImpl(
    private val dataMallApiService: DataMallApiService,
    private val apiKey: String
) : SingaporeTravelRepository {

    companion object {
        private const val EARTH_RADIUS_KM = 6371.0
    }

    override suspend fun getBusArrival(busStopCode: String, serviceNo: String?): Result<BusArrivalResponse> {
        return try {
            val response = RetryHelper.retry {
                dataMallApiService.getBusArrival(busStopCode, serviceNo, apiKey)
            }
            val apiResponse: BusArrivalApiResponse = response.body()
            val busArrival = mapToBusArrivalResponse(apiResponse)
            Result.success(busArrival)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getTrainServiceAlerts(): Result<List<TrainAlert>> {
        return try {
            val response = RetryHelper.retry {
                dataMallApiService.getTrainServiceAlerts(apiKey)
            }
            val apiResponse: TrainServiceAlertsApiResponse = response.body()
            val alerts = apiResponse.value.Message.map { dto ->
                TrainAlert(
                    content = dto.Content,
                    createdDate = dto.CreatedDate
                )
            }
            Result.success(alerts)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getPlatformCrowdDensity(trainLine: String): Result<List<PlatformCrowdDensity>> {
        return try {
            val response = RetryHelper.retry {
                dataMallApiService.getPlatformCrowdDensity(trainLine, apiKey)
            }
            val apiResponse: PlatformCrowdDensityApiResponse = response.body()
            val crowdData = apiResponse.value.map { dto ->
                PlatformCrowdDensity(
                    stationCode = dto.Station,
                    crowdLevel = dto.CrowdLevel,
                    startTime = dto.StartTime,
                    endTime = dto.EndTime
                )
            }
            Result.success(crowdData)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getBusStops(skip: Int): Result<List<BusStop>> {
        return try {
            val response = RetryHelper.retry {
                dataMallApiService.getBusStops(skip, apiKey)
            }
            val apiResponse: BusStopsApiResponse = response.body()
            val busStops = apiResponse.value.map { dto ->
                BusStop(
                    busStopCode = dto.BusStopCode,
                    roadName = dto.RoadName,
                    description = dto.Description,
                    latitude = dto.Latitude,
                    longitude = dto.Longitude
                )
            }
            Result.success(busStops)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getTrainStations(skip: Int): Result<List<MrtStation>> {
        return try {
            val response = RetryHelper.retry {
                dataMallApiService.getTrainStations(skip, apiKey)
            }
            val apiResponse: TrainStationsApiResponse = response.body()
            val stations = apiResponse.value.map { dto ->
                MrtStation(
                    stationCode = dto.StationCode,
                    stationName = dto.StationName,
                    latitude = dto.Latitude,
                    longitude = dto.Longitude
                )
            }
            Result.success(stations)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getNearbyBusStops(location: LocationData, radiusKm: Double): Result<List<BusStopWithArrivals>> {
        return try {
            // Get all bus stops (in a real implementation, you'd want to implement pagination)
            val busStopsResult = getBusStops()
            if (busStopsResult.isFailure) {
                return Result.failure(busStopsResult.exceptionOrNull()!!)
            }

            val busStops = busStopsResult.getOrNull()!!
            val nearbyStops = busStops.filter { busStop ->
                calculateDistance(location.latitude, location.longitude, busStop.latitude, busStop.longitude) <= radiusKm
            }

            val stopsWithArrivals = nearbyStops.map { busStop ->
                val distance = calculateDistance(location.latitude, location.longitude, busStop.latitude, busStop.longitude)
                val arrivalResult = getBusArrival(busStop.busStopCode)
                BusStopWithArrivals(
                    busStop = busStop,
                    arrivals = arrivalResult.getOrNull(),
                    distance = distance
                )
            }.sortedBy { it.distance }

            Result.success(stopsWithArrivals)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getNearbyMrtStations(location: LocationData, radiusKm: Double): Result<List<MrtStationWithInfo>> {
        return try {
            val stationsResult = getTrainStations()
            if (stationsResult.isFailure) {
                return Result.failure(stationsResult.exceptionOrNull()!!)
            }

            val stations = stationsResult.getOrNull()!!
            val nearbyStations = stations.filter { station ->
                calculateDistance(location.latitude, location.longitude, station.latitude, station.longitude) <= radiusKm
            }

            val crowdResult = getPlatformCrowdDensity("EWL")
            val crowdData = crowdResult.getOrNull() ?: emptyList()

            val stationsWithInfo = nearbyStations.map { station ->
                val distance = calculateDistance(location.latitude, location.longitude, station.latitude, station.longitude)
                val crowdInfo = crowdData.find { it.stationCode == station.stationCode }
                MrtStationWithInfo(
                    station = station,
                    crowdLevel = crowdInfo,
                    distance = distance
                )
            }.sortedBy { it.distance }

            Result.success(stationsWithInfo)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getSingaporeTravelInfo(location: LocationData): Result<SingaporeTravelInfo> {
        return try {
            val nearbyBusStopsResult = getNearbyBusStops(location)
            val nearbyMrtStationsResult = getNearbyMrtStations(location)
            val trainAlertsResult = getTrainServiceAlerts()

            val travelInfo = SingaporeTravelInfo(
                nearbyBusStops = nearbyBusStopsResult.getOrNull() ?: emptyList(),
                nearbyMrtStations = nearbyMrtStationsResult.getOrNull() ?: emptyList(),
                trainAlerts = trainAlertsResult.getOrNull() ?: emptyList(),
                lastUpdated = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).toString()
            )

            Result.success(travelInfo)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private fun calculateDistance(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {
        val dLat = kotlin.math.PI / 180 * (lat2 - lat1)
        val dLon = kotlin.math.PI / 180 * (lon2 - lon1)
        val a = sin(dLat / 2) * sin(dLat / 2) +
                cos(kotlin.math.PI / 180 * lat1) * cos(kotlin.math.PI / 180 * lat2) *
                sin(dLon / 2) * sin(dLon / 2)
        val c = 2 * atan2(sqrt(a), sqrt(1 - a))
        return EARTH_RADIUS_KM * c
    }

    private fun mapToBusArrivalResponse(apiResponse: BusArrivalApiResponse): BusArrivalResponse {
        return BusArrivalResponse(
            busStopCode = apiResponse.BusStopCode,
            services = apiResponse.Services.map { serviceDto ->
                BusService(
                    serviceNo = serviceDto.ServiceNo,
                    operator = serviceDto.Operator,
                    nextBus = mapToBusInfo(serviceDto.NextBus),
                    nextBus2 = serviceDto.NextBus2?.let { mapToBusInfo(it) },
                    nextBus3 = serviceDto.NextBus3?.let { mapToBusInfo(it) }
                )
            }
        )
    }

    private fun mapToBusInfo(busInfoDto: BusInfoDto): BusInfo {
        return BusInfo(
            originCode = busInfoDto.OriginCode,
            destinationCode = busInfoDto.DestinationCode,
            estimatedArrival = busInfoDto.EstimatedArrival,
            latitude = busInfoDto.Latitude,
            longitude = busInfoDto.Longitude,
            visitNumber = busInfoDto.VisitNumber,
            load = busInfoDto.Load,
            feature = busInfoDto.Feature,
            type = busInfoDto.Type
        )
    }
}