package com.cherry.kmp.datamall.domain.model

import kotlinx.serialization.Serializable

/**
 * Bus arrival information from DataMall API
 */
@Serializable
data class BusArrivalResponse(
    val busStopCode: String,
    val services: List<BusService>
)

@Serializable
data class BusService(
    val serviceNo: String,
    val operator: String,
    val nextBus: BusInfo,
    val nextBus2: BusInfo?,
    val nextBus3: BusInfo?
)

@Serializable
data class BusInfo(
    val originCode: String,
    val destinationCode: String,
    val estimatedArrival: String,
    val latitude: String,
    val longitude: String,
    val visitNumber: String,
    val load: String, // SEA (Seats Available), SDA (Standing Available), LSD (Limited Standing)
    val feature: String, // WAB (Wheelchair Accessible Bus)
    val type: String // SD (Single Deck), DD (Double Deck), BD (Bendy)
)

/**
 * MRT/Train related data models
 */
@Serializable
data class TrainServiceAlert(
    val status: Int,
    val message: List<TrainAlert>
)

@Serializable
data class TrainAlert(
    val content: String,
    val createdDate: String
)

@Serializable
data class PlatformCrowdDensity(
    val stationCode: String,
    val crowdLevel: String, // l (Low), m (Moderate), h (High)
    val startTime: String,   // Start of time interval
    val endTime: String      // End of time interval
)

/**
 * Bus stop information
 */
@Serializable
data class BusStop(
    val busStopCode: String,
    val roadName: String,
    val description: String,
    val latitude: Double,
    val longitude: Double
)

/**
 * MRT station information
 */
@Serializable
data class MrtStation(
    val stationCode: String,
    val stationName: String,
    val latitude: Double,
    val longitude: Double
)

/**
 * Combined travel information for UI
 */
data class SingaporeTravelInfo(
    val nearbyBusStops: List<BusStopWithArrivals> = emptyList(),
    val nearbyMrtStations: List<MrtStationWithInfo> = emptyList(),
    val trainAlerts: List<TrainAlert> = emptyList(),
    val lastUpdated: String = ""
)

data class BusStopWithArrivals(
    val busStop: BusStop,
    val arrivals: BusArrivalResponse?,
    val distance: Double? = null
)

data class MrtStationWithInfo(
    val station: MrtStation,
    val crowdLevel: PlatformCrowdDensity?,
    val distance: Double? = null
)

/**
 * Location data for finding nearby transport
 */
data class LocationData(
    val latitude: Double,
    val longitude: Double
)