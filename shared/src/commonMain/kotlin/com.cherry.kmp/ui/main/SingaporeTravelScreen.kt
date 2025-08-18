package com.cherry.kmp.ui.main

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.lerp
import androidx.navigation.NavHostController
import com.cherry.kmp.core.domain.UiState
import com.cherry.kmp.datamall.ui.viewmodel.SingaporeTravelViewModel
import com.cherry.kmp.datamall.domain.model.*
import com.cherry.kmp.ui.component.ErrorScreen
import com.cherry.kmp.ui.component.LoadingScreen
import com.cherry.kmp.ui.navigation.NavigationRoutes
import com.cherry.kmp.core.ui.theme.MinimalistColors
import org.koin.compose.koinInject
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun SingaporeTravelScreen(
    viewModel: SingaporeTravelViewModel = koinInject(),
    navController: NavHostController
) {
    var isScreenLoaded by remember { mutableStateOf(false) }
    
    val travelInfoState by viewModel.travelInfoState.collectAsState()
    val trainAlertsState by viewModel.trainAlertsState.collectAsState()
    val listState = rememberLazyListState()
    
    LaunchedEffect(Unit) {
        isScreenLoaded = true
        // Load travel data with default Singapore location
        val singaporeLocation = LocationData(latitude = 1.3521, longitude = 103.8198)
        viewModel.loadTravelInfo(singaporeLocation)
        viewModel.loadTrainServiceAlerts()
    }
    
    val screenAnimationScale by animateFloatAsState(
        targetValue = if (isScreenLoaded) 1f else 0.95f,
        animationSpec = tween(
            durationMillis = 400,
            easing = FastOutSlowInEasing
        )
    )

    val scrollProgress = remember(listState.firstVisibleItemIndex, listState.firstVisibleItemScrollOffset) {
        when {
            listState.firstVisibleItemIndex > 1 -> 1f
            listState.firstVisibleItemIndex == 1 -> (listState.firstVisibleItemScrollOffset / 300f).coerceIn(0f, 1f)
            else -> 0f
        }
    }

    Scaffold(
        topBar = {
            CollapsingSingaporeTravelHeader(
                scrollProgress = scrollProgress
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .scale(screenAnimationScale)
        ) {
            when (val state = travelInfoState) {
                is UiState.Loading -> LoadingScreen()
                is UiState.Success -> {
                    val trainAlerts = when (val alertsState = trainAlertsState) {
                        is UiState.Success -> alertsState.data
                        is UiState.CachedSuccess -> alertsState.data
                        else -> emptyList()
                    }
                    SingaporeTravelMainContent(
                        travelInfo = state.data,
                        trainAlerts = trainAlerts,
                        listState = listState,
                        scrollProgress = scrollProgress,
                        navController = navController
                    )
                }
                is UiState.Error -> {
                    ErrorScreen(
                        errorMessage = state.apiError.message ?: "Unable to load travel information",
                        onRetry = { 
                            val singaporeLocation = LocationData(latitude = 1.3521, longitude = 103.8198)
                            viewModel.loadTravelInfo(singaporeLocation)
                        }
                    )
                }
                is UiState.CachedSuccess -> {
                    val trainAlerts = when (val alertsState = trainAlertsState) {
                        is UiState.Success -> alertsState.data
                        is UiState.CachedSuccess -> alertsState.data
                        else -> emptyList()
                    }
                    SingaporeTravelMainContent(
                        travelInfo = state.data,
                        trainAlerts = trainAlerts,
                        listState = listState,
                        scrollProgress = scrollProgress,
                        navController = navController
                    )
                }
                is UiState.Initial -> LoadingScreen()
            }
        }
    }
}

@Composable
private fun CollapsingSingaporeTravelHeader(
    scrollProgress: Float
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = lerp(0.dp, 8.dp, scrollProgress)),
        colors = CardDefaults.cardColors(
            containerColor = MinimalistColors.PrimarySurface
        ),
        shape = RoundedCornerShape(
            bottomStart = lerp(24.dp, 0.dp, scrollProgress),
            bottomEnd = lerp(24.dp, 0.dp, scrollProgress)
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.Transparent)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 20.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(lerp(0.dp, 12.dp, scrollProgress))
                    ) {
                        Column {
                            Text(
                                text = "🇸🇬 Singapore Travel",
                                style = MaterialTheme.typography.headlineSmall.copy(
                                    fontWeight = FontWeight.Bold
                                ),
                                color = MinimalistColors.PrimaryText
                            )
                            Text(
                                text = "Real-time transport information",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MinimalistColors.SecondaryText.copy(alpha = 0.7f),
                                modifier = Modifier.alpha(1f - (scrollProgress * 0.3f))
                            )
                        }
                        
                        if (scrollProgress > 0.3f) {
                            Icon(
                                imageVector = Icons.Default.LocationOn,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary.copy(alpha = scrollProgress),
                                modifier = Modifier
                                    .size(lerp(0.dp, 24.dp, scrollProgress))
                                    .alpha(scrollProgress)
                            )
                        }
                    }
                    
                    Card(
                        modifier = Modifier.size(48.dp),
                        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MinimalistColors.SecondarySurface
                        ),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Refresh,
                                contentDescription = "Refresh",
                                tint = MinimalistColors.PrimaryText.copy(alpha = 0.8f),
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun SingaporeTravelMainContent(
    travelInfo: SingaporeTravelInfo,
    trainAlerts: List<TrainAlert>,
    listState: androidx.compose.foundation.lazy.LazyListState,
    scrollProgress: Float,
    navController: NavHostController
) {
    LazyColumn(
        state = listState,
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Column(
                modifier = Modifier
                    .alpha(1f - scrollProgress)
                    .padding(bottom = 16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                TravelHeaderSection()
                TravelTabSelection(navController = navController)
            }
        }
        
        item {
            TrainAlertsCard(alerts = trainAlerts)
        }
        
        item {
            NearbyBusStopsCard(
                busStops = travelInfo.nearbyBusStops.take(3),
                onViewAll = { navController.navigate(NavigationRoutes.BusArrivals.route) }
            )
        }
        
        item {
            NearbyMrtStationsCard(
                mrtStations = travelInfo.nearbyMrtStations.take(3),
                onViewAll = { navController.navigate(NavigationRoutes.MrtStations.route) }
            )
        }
        
        item {
            LastUpdatedCard(lastUpdated = travelInfo.lastUpdated)
        }
    }
}


@Composable
private fun TravelHeaderSection() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MinimalistColors.SecondarySurface
        ),
        shape = RoundedCornerShape(24.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.Transparent)
                .padding(24.dp)
        ) {
            Column {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(
                        imageVector = Icons.Default.LocationOn,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.8f),
                        modifier = Modifier.size(32.dp)
                    )
                    
                    Spacer(modifier = Modifier.width(16.dp))
                    
                    Column {
                        Text(
                            text = "Singapore Transport",
                            style = MaterialTheme.typography.headlineSmall.copy(
                                fontWeight = FontWeight.Bold
                            ),
                            color = MinimalistColors.PrimaryText
                        )
                        
                        Text(
                            text = "Real-time bus arrivals and MRT information",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MinimalistColors.PrimaryText.copy(alpha = 0.7f)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun TravelTabSelection(navController: NavHostController) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
        colors = CardDefaults.cardColors(
            containerColor = MinimalistColors.SecondarySurface
        ),
        shape = RoundedCornerShape(20.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            Text(
                text = "Browse Transport",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.SemiBold
                ),
                color = MinimalistColors.PrimaryText,
                modifier = Modifier.padding(bottom = 16.dp)
            )
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Card(
                    onClick = { navController.navigate(NavigationRoutes.TravelOverview.route) },
                    modifier = Modifier.weight(1f),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MinimalistColors.PrimarySurface
                    ),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = Icons.Default.Dashboard,
                            contentDescription = null,
                            modifier = Modifier.size(24.dp),
                            tint = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Overview",
                            style = MaterialTheme.typography.labelMedium,
                            color = MinimalistColors.PrimaryText,
                            textAlign = TextAlign.Center
                        )
                    }
                }
                
                Card(
                    onClick = { navController.navigate(NavigationRoutes.BusArrivals.route) },
                    modifier = Modifier.weight(1f),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MinimalistColors.PrimarySurface
                    ),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = Icons.Default.DirectionsBus,
                            contentDescription = null,
                            modifier = Modifier.size(24.dp),
                            tint = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Bus",
                            style = MaterialTheme.typography.labelMedium,
                            color = MinimalistColors.PrimaryText,
                            textAlign = TextAlign.Center
                        )
                    }
                }
                
                Card(
                    onClick = { navController.navigate(NavigationRoutes.MrtStations.route) },
                    modifier = Modifier.weight(1f),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MinimalistColors.PrimarySurface
                    ),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = Icons.Default.Train,
                            contentDescription = null,
                            modifier = Modifier.size(24.dp),
                            tint = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "MRT",
                            style = MaterialTheme.typography.labelMedium,
                            color = MinimalistColors.PrimaryText,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        }
    }
}

// Additional composables for bus stops, MRT stations, etc. will be created here
@Composable
private fun TrainAlertsCard(alerts: List<TrainAlert>) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (alerts.isNotEmpty()) MinimalistColors.PrimarySurface else MinimalistColors.SecondarySurface
        ),
        shape = RoundedCornerShape(20.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = if (alerts.isNotEmpty()) Icons.Default.Warning else Icons.Default.CheckCircle,
                    contentDescription = null,
                    tint = if (alerts.isNotEmpty()) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = "Service Alerts",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.SemiBold
                    ),
                    color = MinimalistColors.PrimaryText
                )
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            if (alerts.isEmpty()) {
                Text(
                    text = "All services running normally",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MinimalistColors.PrimaryText.copy(alpha = 0.7f)
                )
            } else {
                alerts.take(2).forEach { alert ->
                    Card(
                        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.3f)
                        ),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(
                            text = alert.content,
                            style = MaterialTheme.typography.bodySmall,
                            color = MinimalistColors.PrimaryText,
                            modifier = Modifier.padding(12.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun NearbyBusStopsCard(
    busStops: List<BusStopWithArrivals>,
    onViewAll: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
        colors = CardDefaults.cardColors(
            containerColor = MinimalistColors.SecondarySurface
        ),
        shape = RoundedCornerShape(20.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.DirectionsBus,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = "Nearby Bus Stops",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.SemiBold
                        ),
                        color = MinimalistColors.PrimaryText
                    )
                }
                
                TextButton(onClick = onViewAll) {
                    Text("View All")
                }
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            if (busStops.isEmpty()) {
                Text(
                    text = "No nearby bus stops found",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MinimalistColors.PrimaryText.copy(alpha = 0.7f)
                )
            } else {
                busStops.forEach { busStopWithArrivals ->
                    BusStopItem(busStopWithArrivals = busStopWithArrivals)
                    if (busStopWithArrivals != busStops.last()) {
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                }
            }
        }
    }
}

@Composable
private fun NearbyMrtStationsCard(
    mrtStations: List<MrtStationWithInfo>,
    onViewAll: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
        colors = CardDefaults.cardColors(
            containerColor = MinimalistColors.SecondarySurface
        ),
        shape = RoundedCornerShape(20.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Train,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = "Nearby MRT Stations",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.SemiBold
                        ),
                        color = MinimalistColors.PrimaryText
                    )
                }
                
                TextButton(onClick = onViewAll) {
                    Text("View All")
                }
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            if (mrtStations.isEmpty()) {
                Text(
                    text = "No nearby MRT stations found",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MinimalistColors.PrimaryText.copy(alpha = 0.7f)
                )
            } else {
                mrtStations.forEach { mrtStationWithInfo ->
                    MrtStationItem(mrtStationWithInfo = mrtStationWithInfo)
                    if (mrtStationWithInfo != mrtStations.last()) {
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                }
            }
        }
    }
}

@Composable
private fun BusStopItem(busStopWithArrivals: BusStopWithArrivals) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MinimalistColors.PrimarySurface
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = busStopWithArrivals.busStop.description,
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontWeight = FontWeight.SemiBold
                ),
                color = MinimalistColors.PrimaryText
            )
            Text(
                text = "Stop ${busStopWithArrivals.busStop.busStopCode} • ${busStopWithArrivals.distance?.let { "${(it * 10).toInt() / 10.0} km" } ?: ""}",
                style = MaterialTheme.typography.bodySmall,
                color = MinimalistColors.PrimaryText.copy(alpha = 0.7f)
            )
            
            busStopWithArrivals.arrivals?.services?.take(2)?.forEach { service ->
                Row(
                    modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Bus ${service.serviceNo}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MinimalistColors.PrimaryText
                    )
                    Text(
                        text = formatArrivalTime(service.nextBus.estimatedArrival),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }
    }
}

@Composable
private fun MrtStationItem(mrtStationWithInfo: MrtStationWithInfo) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MinimalistColors.PrimarySurface
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = mrtStationWithInfo.station.stationName,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontWeight = FontWeight.SemiBold
                    ),
                    color = MinimalistColors.PrimaryText
                )
                Text(
                    text = "${mrtStationWithInfo.station.stationCode} • ${mrtStationWithInfo.distance?.let { "${(it * 10).toInt() / 10.0} km" } ?: ""}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MinimalistColors.PrimaryText.copy(alpha = 0.7f)
                )
            }
            
            mrtStationWithInfo.crowdLevel?.let { crowdLevel ->
                val (crowdText, crowdColor) = when (crowdLevel.crowdLevel.lowercase()) {
                    "l" -> "Low" to MaterialTheme.colorScheme.primary
                    "m" -> "Moderate" to MaterialTheme.colorScheme.tertiary
                    "h" -> "High" to MaterialTheme.colorScheme.error
                    else -> "Unknown" to MinimalistColors.PrimaryText
                }
                
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = crowdColor.copy(alpha = 0.2f)
                    ),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = crowdText,
                        style = MaterialTheme.typography.labelSmall,
                        color = crowdColor,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun LastUpdatedCard(lastUpdated: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(
            containerColor = MinimalistColors.PrimarySurface
        ),
        shape = RoundedCornerShape(16.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.AccessTime,
                contentDescription = null,
                tint = MinimalistColors.PrimaryText.copy(alpha = 0.7f),
                modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "Last updated: ${formatLastUpdated(lastUpdated)}",
                style = MaterialTheme.typography.bodySmall,
                color = MinimalistColors.PrimaryText.copy(alpha = 0.7f)
            )
        }
    }
}



private fun formatArrivalTime(estimatedArrival: String): String {
    return try {
        val arrivalInstant = Instant.parse(estimatedArrival)
        val currentInstant = kotlinx.datetime.Clock.System.now()
        val diff = arrivalInstant - currentInstant
        val minutes = diff.inWholeMinutes
        
        when {
            minutes <= 0 -> "Arr"
            minutes == 1L -> "1 min"
            minutes < 60 -> "${minutes} mins"
            else -> "${minutes / 60}h ${minutes % 60}m"
        }
    } catch (e: Exception) {
        "N/A"
    }
}

private fun formatLastUpdated(lastUpdated: String): String {
    return try {
        val instant = Instant.parse(lastUpdated)
        val localDateTime = instant.toLocalDateTime(TimeZone.currentSystemDefault())
        "${localDateTime.hour.toString().padStart(2, '0')}:${localDateTime.minute.toString().padStart(2, '0')}"
    } catch (e: Exception) {
        "Unknown"
    }
}