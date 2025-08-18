package com.cherry.kmp.datamall.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import com.cherry.kmp.core.common.LoggerConfig
import com.cherry.kmp.core.domain.UiState
import com.cherry.kmp.core.domain.exception.ApiException
import com.cherry.kmp.datamall.domain.model.*
import com.cherry.kmp.datamall.domain.usecase.*

class SingaporeTravelViewModel(
    private val getSingaporeTravelInfoUseCase: GetSingaporeTravelInfoUseCase,
    private val getNearbyBusStopsUseCase: GetNearbyBusStopsUseCase,
    private val getNearbyMrtStationsUseCase: GetNearbyMrtStationsUseCase,
    private val getBusArrivalUseCase: GetBusArrivalUseCase,
    private val getTrainServiceAlertsUseCase: GetTrainServiceAlertsUseCase,
    private val getPlatformCrowdDensityUseCase: GetPlatformCrowdDensityUseCase
) : ViewModel() {

    // Track ongoing jobs for proper cleanup
    private var travelInfoJob: Job? = null
    private var busStopsJob: Job? = null
    private var mrtStationsJob: Job? = null
    private var busArrivalJob: Job? = null
    private var trainAlertsJob: Job? = null
    private var crowdDensityJob: Job? = null

    // StateFlow for consistent state management
    private val _travelInfoState = MutableStateFlow<UiState<SingaporeTravelInfo>>(UiState.Initial)
    val travelInfoState: StateFlow<UiState<SingaporeTravelInfo>> = _travelInfoState.asStateFlow()

    private val _busStopsState = MutableStateFlow<UiState<List<BusStopWithArrivals>>>(UiState.Initial)
    val busStopsState: StateFlow<UiState<List<BusStopWithArrivals>>> = _busStopsState.asStateFlow()

    private val _mrtStationsState = MutableStateFlow<UiState<List<MrtStationWithInfo>>>(UiState.Initial)
    val mrtStationsState: StateFlow<UiState<List<MrtStationWithInfo>>> = _mrtStationsState.asStateFlow()

    private val _busArrivalState = MutableStateFlow<UiState<BusArrivalResponse>>(UiState.Initial)
    val busArrivalState: StateFlow<UiState<BusArrivalResponse>> = _busArrivalState.asStateFlow()

    private val _trainAlertsState = MutableStateFlow<UiState<List<TrainAlert>>>(UiState.Initial)
    val trainAlertsState: StateFlow<UiState<List<TrainAlert>>> = _trainAlertsState.asStateFlow()

    private val _crowdDensityState = MutableStateFlow<UiState<List<PlatformCrowdDensity>>>(UiState.Initial)
    val crowdDensityState: StateFlow<UiState<List<PlatformCrowdDensity>>> = _crowdDensityState.asStateFlow()

    // Current location state
    private val _currentLocation = MutableStateFlow<LocationData?>(null)
    val currentLocation: StateFlow<LocationData?> = _currentLocation.asStateFlow()

    /**
     * Load comprehensive Singapore travel information
     */
    fun loadTravelInfo(location: LocationData) {
        LoggerConfig.logger.d { "Loading Singapore travel info for location: ${location.latitude}, ${location.longitude}" }
        _currentLocation.value = location
        
        travelInfoJob?.cancel()
        travelInfoJob = viewModelScope.launch {
            _travelInfoState.value = UiState.Loading
            getSingaporeTravelInfoUseCase(location)
                .catch { exception ->
                    LoggerConfig.logger.e(exception) { "Error loading travel info" }
                    _travelInfoState.value = UiState.Error(
                        ApiException.NetworkException()
                    )
                }
                .collect { result ->
                    LoggerConfig.logger.d { 
                        "Travel info loaded: ${if (result is UiState.Success) 
                            "Success with ${result.data.nearbyBusStops.size} bus stops, ${result.data.nearbyMrtStations.size} MRT stations" 
                            else result::class.simpleName}" 
                    }
                    _travelInfoState.value = result
                    
                    // Also update individual states for tab views
                    if (result is UiState.Success) {
                        _busStopsState.value = UiState.Success(result.data.nearbyBusStops)
                        _mrtStationsState.value = UiState.Success(result.data.nearbyMrtStations)
                        _trainAlertsState.value = UiState.Success(result.data.trainAlerts)
                    }
                }
        }
    }

    /**
     * Load nearby bus stops with arrivals
     */
    fun loadNearbyBusStops(location: LocationData, radiusKm: Double = 1.0) {
        LoggerConfig.logger.d { "Loading nearby bus stops for radius: ${radiusKm}km" }
        
        busStopsJob?.cancel()
        busStopsJob = viewModelScope.launch {
            _busStopsState.value = UiState.Loading
            getNearbyBusStopsUseCase(NearbyTransportParams(location, radiusKm))
                .catch { exception ->
                    LoggerConfig.logger.e(exception) { "Error loading nearby bus stops" }
                    _busStopsState.value = UiState.Error(
                        ApiException.NetworkException()
                    )
                }
                .collect { result ->
                    LoggerConfig.logger.d { 
                        "Bus stops loaded: ${if (result is UiState.Success) 
                            "Success with ${result.data.size} stops" 
                            else result::class.simpleName}" 
                    }
                    _busStopsState.value = result
                }
        }
    }

    /**
     * Load nearby MRT stations with crowd info
     */
    fun loadNearbyMrtStations(location: LocationData, radiusKm: Double = 1.0) {
        LoggerConfig.logger.d { "Loading nearby MRT stations for radius: ${radiusKm}km" }
        
        mrtStationsJob?.cancel()
        mrtStationsJob = viewModelScope.launch {
            _mrtStationsState.value = UiState.Loading
            getNearbyMrtStationsUseCase(NearbyTransportParams(location, radiusKm))
                .catch { exception ->
                    LoggerConfig.logger.e(exception) { "Error loading nearby MRT stations" }
                    _mrtStationsState.value = UiState.Error(
                        ApiException.NetworkException()
                    )
                }
                .collect { result ->
                    LoggerConfig.logger.d { 
                        "MRT stations loaded: ${if (result is UiState.Success) 
                            "Success with ${result.data.size} stations" 
                            else result::class.simpleName}" 
                    }
                    _mrtStationsState.value = result
                }
        }
    }

    /**
     * Load bus arrival for a specific bus stop
     */
    fun loadBusArrival(busStopCode: String, serviceNo: String? = null) {
        LoggerConfig.logger.d { "Loading bus arrival for stop: $busStopCode, service: $serviceNo" }
        
        busArrivalJob?.cancel()
        busArrivalJob = viewModelScope.launch {
            _busArrivalState.value = UiState.Loading
            getBusArrivalUseCase(BusArrivalParams(busStopCode, serviceNo))
                .catch { exception ->
                    LoggerConfig.logger.e(exception) { "Error loading bus arrival" }
                    _busArrivalState.value = UiState.Error(
                        ApiException.NetworkException()
                    )
                }
                .collect { result ->
                    LoggerConfig.logger.d { 
                        "Bus arrival loaded: ${if (result is UiState.Success) 
                            "Success with ${result.data.services.size} services" 
                            else result::class.simpleName}" 
                    }
                    _busArrivalState.value = result
                }
        }
    }

    /**
     * Load train service alerts
     */
    fun loadTrainServiceAlerts() {
        LoggerConfig.logger.d { "Loading train service alerts" }
        
        trainAlertsJob?.cancel()
        trainAlertsJob = viewModelScope.launch {
            _trainAlertsState.value = UiState.Loading
            getTrainServiceAlertsUseCase(Unit)
                .catch { exception ->
                    LoggerConfig.logger.e(exception) { "Error loading train service alerts" }
                    _trainAlertsState.value = UiState.Error(
                        ApiException.NetworkException()
                    )
                }
                .collect { result ->
                    LoggerConfig.logger.d { 
                        "Train alerts loaded: ${if (result is UiState.Success) 
                            "Success with ${result.data.size} alerts" 
                            else result::class.simpleName}" 
                    }
                    _trainAlertsState.value = result
                }
        }
    }

    /**
     * Load platform crowd density
     */
    fun loadPlatformCrowdDensity(trainLine: String? = null) {
        LoggerConfig.logger.d { "Loading platform crowd density for line: $trainLine" }
        
        crowdDensityJob?.cancel()
        crowdDensityJob = viewModelScope.launch {
            _crowdDensityState.value = UiState.Loading
            getPlatformCrowdDensityUseCase(trainLine)
                .catch { exception ->
                    LoggerConfig.logger.e(exception) { "Error loading platform crowd density" }
                    _crowdDensityState.value = UiState.Error(
                        ApiException.NetworkException()
                    )
                }
                .collect { result ->
                    LoggerConfig.logger.d { 
                        "Crowd density loaded: ${if (result is UiState.Success) 
                            "Success with ${result.data.size} entries" 
                            else result::class.simpleName}" 
                    }
                    _crowdDensityState.value = result
                }
        }
    }

    /**
     * Refresh all travel data for current location
     */
    fun refreshAllData() {
        LoggerConfig.logger.d { "Refreshing all Singapore travel data..." }
        _currentLocation.value?.let { location ->
            loadTravelInfo(location)
        } ?: run {
            LoggerConfig.logger.w { "No current location available for refresh" }
            // Use default Singapore location
            val defaultLocation = LocationData(latitude = 1.3521, longitude = 103.8198)
            loadTravelInfo(defaultLocation)
        }
    }

    /**
     * Update current location and refresh data
     */
    fun updateLocation(location: LocationData) {
        LoggerConfig.logger.d { "Updating location to: ${location.latitude}, ${location.longitude}" }
        _currentLocation.value = location
        loadTravelInfo(location)
    }

    /**
     * Get travel info for a specific location without changing current location
     */
    fun getTravelInfoForLocation(location: LocationData) {
        LoggerConfig.logger.d { "Getting travel info for specific location: ${location.latitude}, ${location.longitude}" }
        loadTravelInfo(location)
    }

    /**
     * Cancel all ongoing operations
     */
    fun cancelAllOperations() {
        LoggerConfig.logger.d { "Cancelling all ongoing travel operations..." }
        travelInfoJob?.cancel()
        busStopsJob?.cancel()
        mrtStationsJob?.cancel()
        busArrivalJob?.cancel()
        trainAlertsJob?.cancel()
        crowdDensityJob?.cancel()
    }

    /**
     * Proper cleanup when ViewModel is cleared
     */
    override fun onCleared() {
        super.onCleared()
        LoggerConfig.logger.d { "SingaporeTravelViewModel cleared - cancelling ongoing operations" }
        cancelAllOperations()
    }

    /**
     * Check if any operation is currently loading
     */
    fun isLoading(): Boolean {
        return _travelInfoState.value is UiState.Loading ||
               _busStopsState.value is UiState.Loading ||
               _mrtStationsState.value is UiState.Loading ||
               _busArrivalState.value is UiState.Loading ||
               _trainAlertsState.value is UiState.Loading ||
               _crowdDensityState.value is UiState.Loading
    }

    /**
     * Get the last successful travel info data
     */
    fun getLastTravelInfo(): SingaporeTravelInfo? {
        return when (val state = _travelInfoState.value) {
            is UiState.Success -> state.data
            is UiState.CachedSuccess -> state.data
            else -> null
        }
    }
}