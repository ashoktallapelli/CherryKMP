package com.cherry.kmp.datamall.domain.usecase

import com.cherry.kmp.datamall.domain.model.*
import com.cherry.kmp.datamall.domain.repository.SingaporeTravelRepository
import com.cherry.kmp.core.domain.UiState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.collect
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class GetBusArrivalUseCaseTest {

    private val testBusStopCode = "83139"
    private val testServiceNo = "23"
    private val testBusArrival = BusArrivalResponse(
        busStopCode = testBusStopCode,
        services = listOf(
            BusService(
                serviceNo = testServiceNo,
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

    @Test
    fun `invoke should return success when repository succeeds with service number`() = runTest {
        // Given
        val fakeRepository = FakeSingaporeTravelRepository().apply {
            setBusArrivalResult(Result.success(testBusArrival))
        }
        val useCase = GetBusArrivalUseCase(fakeRepository, Dispatchers.Unconfined)
        val params = BusArrivalParams(testBusStopCode, testServiceNo)

        // When - collect all states and get the last (final) one
        val allStates = mutableListOf<UiState<BusArrivalResponse>>()
        useCase(params).collect { allStates.add(it) }

        // Then
        assertTrue(allStates.isNotEmpty(), "Expected at least one state")
        val finalState = allStates.last()
        assertTrue(finalState is UiState.Success, "Expected final state to be Success, but was ${finalState::class.simpleName}")
        assertEquals(testBusArrival, finalState.data)
    }

    @Test
    fun `invoke should return success when repository succeeds without service number`() = runTest {
        // Given
        val fakeRepository = FakeSingaporeTravelRepository().apply {
            setBusArrivalResult(Result.success(testBusArrival))
        }
        val useCase = GetBusArrivalUseCase(fakeRepository, Dispatchers.Unconfined)
        val params = BusArrivalParams(testBusStopCode, null)

        // When - collect all states and get the last (final) one
        val allStates = mutableListOf<UiState<BusArrivalResponse>>()
        useCase(params).collect { allStates.add(it) }

        // Then
        assertTrue(allStates.isNotEmpty(), "Expected at least one state")
        val finalState = allStates.last()
        assertTrue(finalState is UiState.Success, "Expected final state to be Success, but was ${finalState::class.simpleName}")
        assertEquals(testBusArrival, finalState.data)
    }

    @Test
    fun `invoke should return error when repository fails`() = runTest {
        // Given
        val exception = RuntimeException("API error")
        val fakeRepository = FakeSingaporeTravelRepository().apply {
            setBusArrivalResult(Result.failure(exception))
        }
        val useCase = GetBusArrivalUseCase(fakeRepository, Dispatchers.Unconfined)
        val params = BusArrivalParams(testBusStopCode, testServiceNo)

        // When - collect all states and get the last (final) one
        val allStates = mutableListOf<UiState<BusArrivalResponse>>()
        useCase(params).collect { allStates.add(it) }

        // Then
        assertTrue(allStates.isNotEmpty(), "Expected at least one state")
        val finalState = allStates.last()
        assertTrue(finalState is UiState.Error, "Expected final state to be Error, but was ${finalState::class.simpleName}")
        assertEquals(exception, finalState.apiError)
    }
}

// Fake implementation for testing
class FakeSingaporeTravelRepository : SingaporeTravelRepository {
    private var busArrivalResult: Result<BusArrivalResponse>? = null
    private var travelInfoResult: Result<SingaporeTravelInfo>? = null
    private var trainAlertsResult: Result<List<TrainAlert>>? = null
    private var crowdDensityResult: Result<List<PlatformCrowdDensity>>? = null
    private var busStopsResult: Result<List<BusStop>>? = null
    private var nearbyBusStopsResult: Result<List<BusStopWithArrivals>>? = null
    private var trainStationsResult: Result<List<MrtStation>>? = null
    private var nearbyMrtStationsResult: Result<List<MrtStationWithInfo>>? = null

    fun setBusArrivalResult(result: Result<BusArrivalResponse>) {
        busArrivalResult = result
    }

    fun setTravelInfoResult(result: Result<SingaporeTravelInfo>) {
        travelInfoResult = result
    }

    fun setTrainAlertsResult(result: Result<List<TrainAlert>>) {
        trainAlertsResult = result
    }

    fun setCrowdDensityResult(result: Result<List<PlatformCrowdDensity>>) {
        crowdDensityResult = result
    }

    fun setBusStopsResult(result: Result<List<BusStop>>) {
        busStopsResult = result
    }

    fun setNearbyBusStopsResult(result: Result<List<BusStopWithArrivals>>) {
        nearbyBusStopsResult = result
    }

    fun setTrainStationsResult(result: Result<List<MrtStation>>) {
        trainStationsResult = result
    }

    fun setNearbyMrtStationsResult(result: Result<List<MrtStationWithInfo>>) {
        nearbyMrtStationsResult = result
    }

    override suspend fun getBusArrival(busStopCode: String, serviceNo: String?): Result<BusArrivalResponse> {
        return busArrivalResult ?: Result.failure(RuntimeException("No result set"))
    }

    override suspend fun getSingaporeTravelInfo(location: LocationData): Result<SingaporeTravelInfo> {
        return travelInfoResult ?: Result.failure(RuntimeException("No result set"))
    }

    override suspend fun getTrainServiceAlerts(): Result<List<TrainAlert>> {
        return trainAlertsResult ?: Result.failure(RuntimeException("No result set"))
    }

    override suspend fun getPlatformCrowdDensity(trainLine: String?): Result<List<PlatformCrowdDensity>> {
        return crowdDensityResult ?: Result.failure(RuntimeException("No result set"))
    }

    override suspend fun getBusStops(skip: Int): Result<List<BusStop>> {
        return busStopsResult ?: Result.failure(RuntimeException("No result set"))
    }

    override suspend fun getNearbyBusStops(location: LocationData, radiusKm: Double): Result<List<BusStopWithArrivals>> {
        return nearbyBusStopsResult ?: Result.failure(RuntimeException("No result set"))
    }

    override suspend fun getTrainStations(skip: Int): Result<List<MrtStation>> {
        return trainStationsResult ?: Result.failure(RuntimeException("No result set"))
    }

    override suspend fun getNearbyMrtStations(location: LocationData, radiusKm: Double): Result<List<MrtStationWithInfo>> {
        return nearbyMrtStationsResult ?: Result.failure(RuntimeException("No result set"))
    }
}