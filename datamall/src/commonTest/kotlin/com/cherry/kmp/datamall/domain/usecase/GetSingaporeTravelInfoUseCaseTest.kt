package com.cherry.kmp.datamall.domain.usecase

import com.cherry.kmp.datamall.domain.model.*
import com.cherry.kmp.core.domain.UiState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.collect
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class GetSingaporeTravelInfoUseCaseTest {

    private val testLocation = LocationData(latitude = 1.3521, longitude = 103.8198)
    private val testTravelInfo = SingaporeTravelInfo(
        nearbyBusStops = listOf(
            BusStopWithArrivals(
                busStop = BusStop(
                    busStopCode = "83139",
                    roadName = "Marina Bay Sands",
                    description = "Marina Bay Sands",
                    latitude = 1.283,
                    longitude = 103.8607
                ),
                distance = 0.5,
                arrivals = null
            )
        ),
        nearbyMrtStations = listOf(
            MrtStationWithInfo(
                station = MrtStation(
                    stationCode = "CE1",
                    stationName = "Bayfront",
                    latitude = 1.2816,
                    longitude = 103.8587
                ),
                distance = 0.4,
                crowdLevel = null
            )
        ),
        lastUpdated = "2024-01-01T12:00:00Z"
    )

    @Test
    fun `invoke should return success when repository succeeds`() = runTest {
        // Given
        val fakeRepository = FakeSingaporeTravelRepository().apply {
            setTravelInfoResult(Result.success(testTravelInfo))
        }
        val useCase = GetSingaporeTravelInfoUseCase(fakeRepository, Dispatchers.Unconfined)

        // When
        val allStates = mutableListOf<UiState<SingaporeTravelInfo>>()
        useCase(testLocation).collect { allStates.add(it) }

        // Then
        assertTrue(allStates.isNotEmpty())
        val finalState = allStates.last()
        assertTrue(finalState is UiState.Success)
        assertEquals(testTravelInfo, finalState.data)
    }

    @Test
    fun `invoke should return error when repository fails`() = runTest {
        // Given
        val exception = RuntimeException("Network error")
        val fakeRepository = FakeSingaporeTravelRepository().apply {
            setTravelInfoResult(Result.failure(exception))
        }
        val useCase = GetSingaporeTravelInfoUseCase(fakeRepository, Dispatchers.Unconfined)

        // When
        val allStates = mutableListOf<UiState<SingaporeTravelInfo>>()
        useCase(testLocation).collect { allStates.add(it) }

        // Then
        assertTrue(allStates.isNotEmpty())
        val finalState = allStates.last()
        assertTrue(finalState is UiState.Error)
        assertEquals(exception, finalState.apiError)
    }
}