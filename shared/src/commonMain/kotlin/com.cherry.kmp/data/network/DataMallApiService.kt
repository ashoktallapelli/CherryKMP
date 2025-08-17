package com.cherry.kmp.data.network

import com.cherry.kmp.domain.model.*
import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.parameter

/**
 * API service for Singapore DataMall endpoints
 */
class DataMallApiService(private val client: HttpClient) {
    
    companion object {
        private const val BASE_URL = "http://datamall2.mytransport.sg"
        private const val API_KEY_HEADER = "AccountKey"
        
        // API Endpoints
        private const val BUS_ARRIVAL_ENDPOINT = "/ltaodataservice/BusArrivalv2"
        private const val TRAIN_SERVICE_ALERTS_ENDPOINT = "/ltaodataservice/TrainServiceAlerts"
        private const val PLATFORM_CROWD_DENSITY_ENDPOINT = "/ltaodataservice/PCDRealTime"
        private const val BUS_STOPS_ENDPOINT = "/ltaodataservice/BusStops"
        private const val TRAIN_STATIONS_ENDPOINT = "/ltaodataservice/TrainStations"
        
        // Parameters
        private const val PARAM_BUS_STOP_CODE = "BusStopCode"
        private const val PARAM_SERVICE_NO = "ServiceNo"
        private const val PARAM_TRAIN_LINE = "TrainLine"
        private const val PARAM_SKIP = "\$skip"
    }
    
    /**
     * Get real-time bus arrival information for a specific bus stop
     */
    suspend fun getBusArrival(
        busStopCode: String,
        serviceNo: String? = null,
        apiKey: String
    ) = client.get("$BASE_URL$BUS_ARRIVAL_ENDPOINT") {
        header(API_KEY_HEADER, apiKey)
        parameter(PARAM_BUS_STOP_CODE, busStopCode)
        serviceNo?.let { parameter(PARAM_SERVICE_NO, it) }
    }
    
    /**
     * Get train service alerts
     */
    suspend fun getTrainServiceAlerts(apiKey: String) = 
        client.get("$BASE_URL$TRAIN_SERVICE_ALERTS_ENDPOINT") {
            header(API_KEY_HEADER, apiKey)
        }
    
    /**
     * Get real-time platform crowd density
     */
    suspend fun getPlatformCrowdDensity(
        trainLine: String? = null,
        apiKey: String
    ) = client.get("$BASE_URL$PLATFORM_CROWD_DENSITY_ENDPOINT") {
        header(API_KEY_HEADER, apiKey)
        trainLine?.let { parameter(PARAM_TRAIN_LINE, it) }
    }
    
    /**
     * Get bus stops data with pagination
     */
    suspend fun getBusStops(
        skip: Int = 0,
        apiKey: String
    ) = client.get("$BASE_URL$BUS_STOPS_ENDPOINT") {
        header(API_KEY_HEADER, apiKey)
        parameter(PARAM_SKIP, skip)
    }
    
    /**
     * Get train stations data with pagination
     */
    suspend fun getTrainStations(
        skip: Int = 0,
        apiKey: String
    ) = client.get("$BASE_URL$TRAIN_STATIONS_ENDPOINT") {
        header(API_KEY_HEADER, apiKey)
        parameter(PARAM_SKIP, skip)
    }
}

/**
 * Data transfer objects for API responses
 */
@kotlinx.serialization.Serializable
data class BusArrivalApiResponse(
    @kotlinx.serialization.SerialName("odata.metadata")
    val odata_metadata: String,
    val BusStopCode: String,
    val Services: List<BusServiceDto>
)

@kotlinx.serialization.Serializable
data class BusServiceDto(
    val ServiceNo: String,
    val Operator: String,
    val NextBus: BusInfoDto,
    val NextBus2: BusInfoDto?,
    val NextBus3: BusInfoDto?
)

@kotlinx.serialization.Serializable
data class BusInfoDto(
    val OriginCode: String,
    val DestinationCode: String,
    val EstimatedArrival: String,
    val Latitude: String,
    val Longitude: String,
    val VisitNumber: String,
    val Load: String,
    val Feature: String,
    val Type: String
)

@kotlinx.serialization.Serializable
data class TrainServiceAlertsApiResponse(
    @kotlinx.serialization.SerialName("odata.metadata")
    val odata_metadata: String,
    val value: TrainServiceAlertsValue
)

@kotlinx.serialization.Serializable
data class TrainServiceAlertsValue(
    val Status: Int,
    val AffectedSegments: List<String>,
    val Message: List<TrainAlertMessageDto>
)

@kotlinx.serialization.Serializable
data class TrainAlertMessageDto(
    val Content: String,
    val CreatedDate: String
)

@kotlinx.serialization.Serializable
data class PlatformCrowdDensityApiResponse(
    @kotlinx.serialization.SerialName("odata.metadata")
    val odata_metadata: String,
    val value: List<PlatformCrowdDensityDto>
)

@kotlinx.serialization.Serializable
data class PlatformCrowdDensityDto(
    val Station: String,
    val CrowdLevel: String,
    val TimeStamp: String
)

@kotlinx.serialization.Serializable
data class BusStopsApiResponse(
    @kotlinx.serialization.SerialName("odata.metadata")
    val odata_metadata: String,
    val value: List<BusStopDto>
)

@kotlinx.serialization.Serializable
data class BusStopDto(
    val BusStopCode: String,
    val RoadName: String,
    val Description: String,
    val Latitude: Double,
    val Longitude: Double
)

@kotlinx.serialization.Serializable
data class TrainStationsApiResponse(
    @kotlinx.serialization.SerialName("odata.metadata")
    val odata_metadata: String,
    val value: List<TrainStationDto>
)

@kotlinx.serialization.Serializable
data class TrainStationDto(
    val StationCode: String,
    val StationName: String,
    val Latitude: Double,
    val Longitude: Double
)