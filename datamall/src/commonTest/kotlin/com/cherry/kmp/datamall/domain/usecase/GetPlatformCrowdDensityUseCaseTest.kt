package com.cherry.kmp.datamall.domain.usecase

import com.cherry.kmp.datamall.domain.model.PlatformCrowdDensity
import com.cherry.kmp.core.domain.UiState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.flow.first
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class GetPlatformCrowdDensityUseCaseTest {

    private val testCrowdData = listOf(
        PlatformCrowdDensity(
            stationCode = "NS1",
            crowdLevel = "L",
            timestamp = "2024-01-01T12:00:00Z"
        ),
        PlatformCrowdDensity(
            stationCode = "NS2",
            crowdLevel = "M",
            timestamp = "2024-01-01T12:00:00Z"
        ),
        PlatformCrowdDensity(
            stationCode = "NS3",
            crowdLevel = "H",
            timestamp = "2024-01-01T12:00:00Z"
        ),
        PlatformCrowdDensity(
            stationCode = "EW1",
            crowdLevel = "L",
            timestamp = "2024-01-01T12:00:00Z"
        )
    )

    @Test
    fun `invoke should return success when no train line specified`() = runTest {
        // Given
        val fakeRepository = FakeSingaporeTravelRepository().apply {
            setCrowdDensityResult(Result.success(testCrowdData))
        }
        val useCase = GetPlatformCrowdDensityUseCase(fakeRepository, Dispatchers.Unconfined)

        // When
        val result = useCase(null).first()

        // Then
        assertTrue(result is UiState.Success)
        assertEquals(testCrowdData, result.data)
        assertEquals(4, result.data.size)
        assertEquals("NS1", result.data[0].stationCode)
        assertEquals("L", result.data[0].crowdLevel)
    }

    @Test
    fun `invoke should return success when train line specified`() = runTest {
        // Given
        val trainLine = "NS"
        val filteredData = testCrowdData.filter { it.stationCode.startsWith("NS") }
        val fakeRepository = FakeSingaporeTravelRepository().apply {
            setCrowdDensityResult(Result.success(filteredData))
        }
        val useCase = GetPlatformCrowdDensityUseCase(fakeRepository, Dispatchers.Unconfined)

        // When
        val result = useCase(trainLine).first()

        // Then
        assertTrue(result is UiState.Success)
        assertEquals(filteredData, result.data)
        assertEquals(3, result.data.size)
        assertEquals("NS1", result.data[0].stationCode)
        assertEquals("NS2", result.data[1].stationCode)
        assertEquals("NS3", result.data[2].stationCode)
    }

    @Test
    fun `invoke should return different crowd levels`() = runTest {
        // Given
        val fakeRepository = FakeSingaporeTravelRepository().apply {
            setCrowdDensityResult(Result.success(testCrowdData))
        }
        val useCase = GetPlatformCrowdDensityUseCase(fakeRepository, Dispatchers.Unconfined)

        // When
        val result = useCase(null).first()

        // Then
        assertTrue(result is UiState.Success)
        val crowdLevels = result.data.map { it.crowdLevel }.distinct()
        assertEquals(setOf("L", "M", "H"), crowdLevels.toSet())
    }

    @Test
    fun `invoke should return success when no crowd data available`() = runTest {
        // Given
        val fakeRepository = FakeSingaporeTravelRepository().apply {
            setCrowdDensityResult(Result.success(emptyList()))
        }
        val useCase = GetPlatformCrowdDensityUseCase(fakeRepository, Dispatchers.Unconfined)

        // When
        val result = useCase(null).first()

        // Then
        assertTrue(result is UiState.Success)
        assertEquals(emptyList(), result.data)
    }

    @Test
    fun `invoke should return error when repository fails`() = runTest {
        // Given
        val exception = RuntimeException("Platform crowd data service unavailable")
        val fakeRepository = FakeSingaporeTravelRepository().apply {
            setCrowdDensityResult(Result.failure(exception))
        }
        val useCase = GetPlatformCrowdDensityUseCase(fakeRepository, Dispatchers.Unconfined)

        // When
        val result = useCase(null).first()

        // Then
        assertTrue(result is UiState.Error)
        assertEquals(exception, result.apiError)
    }
}