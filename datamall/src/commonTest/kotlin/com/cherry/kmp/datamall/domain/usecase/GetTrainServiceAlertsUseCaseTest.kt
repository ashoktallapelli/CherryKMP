package com.cherry.kmp.datamall.domain.usecase

import com.cherry.kmp.datamall.domain.model.TrainAlert
import com.cherry.kmp.core.domain.UiState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.flow.first
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class GetTrainServiceAlertsUseCaseTest {

    private val testAlerts = listOf(
        TrainAlert(
            content = "NORTH SOUTH LINE - Minor delay in train services towards Marina South Pier between Ang Mo Kio and Toa Payoh due to a signalling fault. Free regular bus services are available.",
            createdDate = "2024-01-01T08:30:00"
        ),
        TrainAlert(
            content = "EAST WEST LINE - Train services are operating normally.",
            createdDate = "2024-01-01T09:00:00"
        ),
        TrainAlert(
            content = "CIRCLE LINE - Disruption in train services between Harbourfront and Stadium due to platform screen door fault. Free shuttle bus service is available.",
            createdDate = "2024-01-01T10:15:00"
        )
    )

    @Test
    fun `invoke should return success when repository succeeds`() = runTest {
        // Given
        val fakeRepository = FakeSingaporeTravelRepository().apply {
            setTrainAlertsResult(Result.success(testAlerts))
        }
        val useCase = GetTrainServiceAlertsUseCase(fakeRepository, Dispatchers.Unconfined)

        // When
        val result = useCase(Unit).first()

        // Then
        assertTrue(result is UiState.Success)
        assertEquals(testAlerts, result.data)
        assertEquals(3, result.data.size)
        assertEquals("NORTH SOUTH LINE - Minor delay in train services towards Marina South Pier between Ang Mo Kio and Toa Payoh due to a signalling fault. Free regular bus services are available.", result.data[0].content)
        assertEquals("2024-01-01T08:30:00", result.data[0].createdDate)
    }

    @Test
    fun `invoke should return success when no alerts available`() = runTest {
        // Given
        val fakeRepository = FakeSingaporeTravelRepository().apply {
            setTrainAlertsResult(Result.success(emptyList()))
        }
        val useCase = GetTrainServiceAlertsUseCase(fakeRepository, Dispatchers.Unconfined)

        // When
        val result = useCase(Unit).first()

        // Then
        assertTrue(result is UiState.Success)
        assertEquals(emptyList(), result.data)
    }

    @Test
    fun `invoke should return error when repository fails`() = runTest {
        // Given
        val exception = RuntimeException("Failed to fetch train alerts")
        val fakeRepository = FakeSingaporeTravelRepository().apply {
            setTrainAlertsResult(Result.failure(exception))
        }
        val useCase = GetTrainServiceAlertsUseCase(fakeRepository, Dispatchers.Unconfined)

        // When
        val result = useCase(Unit).first()

        // Then
        assertTrue(result is UiState.Error)
        assertEquals(exception, result.apiError)
    }
}