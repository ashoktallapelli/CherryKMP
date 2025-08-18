package com.cherry.kmp.datamall.domain.usecase

import com.cherry.kmp.datamall.domain.model.*
import com.cherry.kmp.core.domain.UiState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.flow.first
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class GetNearbyMrtStationsUseCaseTest {

    private val testLocation = LocationData(latitude = 1.3521, longitude = 103.8198)
    private val testMrtStations = listOf(
        MrtStationWithInfo(
            station = MrtStation(
                stationCode = "CE1",
                stationName = "Bayfront",
                latitude = 1.2816,
                longitude = 103.8587
            ),
            distance = 0.4,
            crowdLevel = PlatformCrowdDensity(
                stationCode = "CE1",
                crowdLevel = "L",
                startTime = "2024-01-01T12:00:00Z",
                endTime = "2024-01-01T12:10:00Z"
            )
        ),
        MrtStationWithInfo(
            station = MrtStation(
                stationCode = "DT16",
                stationName = "Bayfront",
                latitude = 1.2816,
                longitude = 103.8587
            ),
            distance = 0.4,
            crowdLevel = PlatformCrowdDensity(
                stationCode = "DT16",
                crowdLevel = "M",
                startTime = "2024-01-01T12:00:00Z",
                endTime = "2024-01-01T12:10:00Z"
            )
        ),
        MrtStationWithInfo(
            station = MrtStation(
                stationCode = "EW13",
                stationName = "City Hall",
                latitude = 1.2930,
                longitude = 103.8520
            ),
            distance = 0.7,
            crowdLevel = null
        )
    )

    @Test
    fun `invoke should return success when repository succeeds with default radius`() = runTest {
        // Given
        val params = NearbyTransportParams(testLocation, 1.0)
        val fakeRepository = FakeSingaporeTravelRepository().apply {
            setNearbyMrtStationsResult(Result.success(testMrtStations))
        }
        val useCase = GetNearbyMrtStationsUseCase(fakeRepository, Dispatchers.Unconfined)

        // When
        val result = useCase(params).first()

        // Then
        assertTrue(result is UiState.Success)
        assertEquals(testMrtStations, result.data)
        assertEquals(3, result.data.size)
        assertEquals("CE1", result.data[0].station.stationCode)
        assertEquals("DT16", result.data[1].station.stationCode)
        assertEquals("EW13", result.data[2].station.stationCode)
    }

    @Test
    fun `invoke should return success when repository succeeds with custom radius`() = runTest {
        // Given
        val customRadius = 0.5
        val params = NearbyTransportParams(testLocation, customRadius)
        val nearbyStations = testMrtStations.take(2) // Only first 2 within 0.5km
        val fakeRepository = FakeSingaporeTravelRepository().apply {
            setNearbyMrtStationsResult(Result.success(nearbyStations))
        }
        val useCase = GetNearbyMrtStationsUseCase(fakeRepository, Dispatchers.Unconfined)

        // When
        val result = useCase(params).first()

        // Then
        assertTrue(result is UiState.Success)
        assertEquals(nearbyStations, result.data)
        assertEquals(2, result.data.size)
    }

    @Test
    fun `invoke should return stations with crowd level information`() = runTest {
        // Given
        val params = NearbyTransportParams(testLocation)
        val fakeRepository = FakeSingaporeTravelRepository().apply {
            setNearbyMrtStationsResult(Result.success(testMrtStations))
        }
        val useCase = GetNearbyMrtStationsUseCase(fakeRepository, Dispatchers.Unconfined)

        // When
        val result = useCase(params).first()

        // Then
        assertTrue(result is UiState.Success)
        assertEquals("L", result.data[0].crowdLevel?.crowdLevel) // Low crowd
        assertEquals("M", result.data[1].crowdLevel?.crowdLevel) // Moderate crowd
        assertEquals(null, result.data[2].crowdLevel) // No crowd data
    }

    @Test
    fun `invoke should return success when no MRT stations found`() = runTest {
        // Given
        val params = NearbyTransportParams(testLocation, 0.1)
        val fakeRepository = FakeSingaporeTravelRepository().apply {
            setNearbyMrtStationsResult(Result.success(emptyList()))
        }
        val useCase = GetNearbyMrtStationsUseCase(fakeRepository, Dispatchers.Unconfined)

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
        val exception = RuntimeException("Service unavailable")
        val fakeRepository = FakeSingaporeTravelRepository().apply {
            setNearbyMrtStationsResult(Result.failure(exception))
        }
        val useCase = GetNearbyMrtStationsUseCase(fakeRepository, Dispatchers.Unconfined)

        // When
        val result = useCase(params).first()

        // Then
        assertTrue(result is UiState.Error)
        assertEquals(exception, result.apiError)
    }
}