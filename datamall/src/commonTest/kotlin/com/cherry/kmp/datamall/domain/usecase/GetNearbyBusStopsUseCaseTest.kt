package com.cherry.kmp.datamall.domain.usecase

import com.cherry.kmp.datamall.domain.model.*
import com.cherry.kmp.core.domain.UiState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.flow.first
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class GetNearbyBusStopsUseCaseTest {

    private val testLocation = LocationData(latitude = 1.3521, longitude = 103.8198)
    private val testBusStops = listOf(
        BusStopWithArrivals(
            busStop = BusStop(
                busStopCode = "83139",
                roadName = "Marina Bay Sands",
                description = "Marina Bay Sands",
                latitude = 1.283,
                longitude = 103.8607
            ),
            distance = 0.5,
            arrivals = BusArrivalResponse(
                busStopCode = "83139",
                services = listOf(
                    BusService(
                        serviceNo = "23",
                        operator = "SBST",
                        nextBus = BusInfo(
                            originCode = "52009",
                            destinationCode = "83139",
                            estimatedArrival = "2024-01-01T12:05:00+08:00",
                            latitude = "1.283",
                            longitude = "103.8607",
                            visitNumber = "1",
                            load = "SEA",
                            feature = "WAB",
                            type = "DD"
                        ),
                        nextBus2 = BusInfo(
                            originCode = "52009",
                            destinationCode = "83139",
                            estimatedArrival = "2024-01-01T12:15:00+08:00",
                            latitude = "",
                            longitude = "",
                            visitNumber = "1",
                            load = "SDA",
                            feature = "",
                            type = "SD"
                        ),
                        nextBus3 = BusInfo(
                            originCode = "52009",
                            destinationCode = "83139",
                            estimatedArrival = "2024-01-01T12:25:00+08:00",
                            latitude = "",
                            longitude = "",
                            visitNumber = "1",
                            load = "LSD",
                            feature = "",
                            type = "DD"
                        )
                    )
                )
            )
        ),
        BusStopWithArrivals(
            busStop = BusStop(
                busStopCode = "83121",
                roadName = "Raffles Avenue",
                description = "Esplanade Bridge",
                latitude = 1.2896,
                longitude = 103.8559
            ),
            distance = 0.8,
            arrivals = null
        )
    )

    @Test
    fun `invoke should return success when repository succeeds with default radius`() = runTest {
        // Given
        val params = NearbyTransportParams(testLocation, 1.0)
        val fakeRepository = FakeSingaporeTravelRepository().apply {
            setNearbyBusStopsResult(Result.success(testBusStops))
        }
        val useCase = GetNearbyBusStopsUseCase(fakeRepository, Dispatchers.Unconfined)

        // When
        val result = useCase(params).first()

        // Then
        assertTrue(result is UiState.Success)
        assertEquals(testBusStops, result.data)
        assertEquals(2, result.data.size)
        assertEquals("83139", result.data[0].busStop.busStopCode)
        assertEquals("83121", result.data[1].busStop.busStopCode)
    }

    @Test
    fun `invoke should return success when repository succeeds with custom radius`() = runTest {
        // Given
        val customRadius = 2.5
        val params = NearbyTransportParams(testLocation, customRadius)
        val fakeRepository = FakeSingaporeTravelRepository().apply {
            setNearbyBusStopsResult(Result.success(testBusStops))
        }
        val useCase = GetNearbyBusStopsUseCase(fakeRepository, Dispatchers.Unconfined)

        // When
        val result = useCase(params).first()

        // Then
        assertTrue(result is UiState.Success)
        assertEquals(testBusStops, result.data)
    }

    @Test
    fun `invoke should return success when no bus stops found`() = runTest {
        // Given
        val params = NearbyTransportParams(testLocation, 0.1)
        val fakeRepository = FakeSingaporeTravelRepository().apply {
            setNearbyBusStopsResult(Result.success(emptyList()))
        }
        val useCase = GetNearbyBusStopsUseCase(fakeRepository, Dispatchers.Unconfined)

        // When
        val result = useCase(params).first()

        // Then
        assertTrue(result is UiState.Success)
        assertEquals(emptyList(), result.data)
    }

    @Test
    fun `invoke should return error when repository fails`() = runTest {
        // Given
        val params = NearbyTransportParams(testLocation)
        val exception = RuntimeException("Network error")
        val fakeRepository = FakeSingaporeTravelRepository().apply {
            setNearbyBusStopsResult(Result.failure(exception))
        }
        val useCase = GetNearbyBusStopsUseCase(fakeRepository, Dispatchers.Unconfined)

        // When
        val result = useCase(params).first()

        // Then
        assertTrue(result is UiState.Error)
        assertEquals(exception, result.apiError)
    }
}