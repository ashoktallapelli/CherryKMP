package com.cherry.kmp.ui.travel

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
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
import com.cherry.kmp.domain.UiState
import com.cherry.kmp.domain.model.*
import com.cherry.kmp.ui.component.ErrorScreen
import com.cherry.kmp.ui.component.LoadingScreen
import com.cherry.kmp.ui.main.viewmodel.SingaporeTravelViewModel
import com.cherry.kmp.ui.navigation.NavigationRoutes
import com.cherry.kmp.ui.theme.MinimalistColors
import org.koin.compose.koinInject
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun TravelOverviewScreen(
    viewModel: SingaporeTravelViewModel = koinInject(),
    navController: NavHostController
) {
    var isScreenLoaded by remember { mutableStateOf(false) }
    
    val travelInfoState by viewModel.travelInfoState.collectAsState()
    val trainAlertsState by viewModel.trainAlertsState.collectAsState()
    val listState = rememberLazyListState()
    
    LaunchedEffect(Unit) {
        isScreenLoaded = true
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
            CollapsingTravelOverviewHeader(
                scrollProgress = scrollProgress,
                onBackClick = { navController.navigateUp() }
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
                    TravelOverviewContent(
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
                    TravelOverviewContent(
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
private fun CollapsingTravelOverviewHeader(
    scrollProgress: Float,
    onBackClick: () -> Unit
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
                        IconButton(onClick = onBackClick) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Back",
                                tint = MinimalistColors.PrimaryText
                            )
                        }
                        
                        Column {
                            Text(
                                text = "🇸🇬 Travel Overview",
                                style = MaterialTheme.typography.headlineSmall.copy(
                                    fontWeight = FontWeight.Bold
                                ),
                                color = MinimalistColors.PrimaryText
                            )
                            Text(
                                text = "Singapore transport summary",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MinimalistColors.SecondaryText.copy(alpha = 0.7f),
                                modifier = Modifier.alpha(1f - (scrollProgress * 0.3f))
                            )
                        }
                        
                        if (scrollProgress > 0.3f) {
                            Icon(
                                imageVector = Icons.Default.Dashboard,
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
private fun TravelOverviewContent(
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
            Text(
                text = "Singapore Transport Overview",
                style = MaterialTheme.typography.headlineSmall.copy(
                    fontWeight = FontWeight.Bold
                ),
                color = MinimalistColors.PrimaryText,
                modifier = Modifier
                    .alpha(1f - scrollProgress)
                    .padding(vertical = 16.dp)
            )
        }
        
        // Quick Actions
        item {
            QuickActionsCard(navController = navController)
        }
        
        // Service Status
        item {
            ServiceStatusCard(trainAlerts = trainAlerts)
        }
        
        // Transport Summary
        item {
            TransportSummaryCard(
                busStopsCount = travelInfo.nearbyBusStops.size,
                mrtStationsCount = travelInfo.nearbyMrtStations.size,
                navController = navController
            )
        }
        
        // Nearby Highlights
        item {
            NearbyHighlightsCard(
                travelInfo = travelInfo,
                navController = navController
            )
        }
        
        // Last Updated
        item {
            LastUpdatedCard(lastUpdated = travelInfo.lastUpdated)
        }
    }
}

@Composable
private fun QuickActionsCard(navController: NavHostController) {
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
                text = "Quick Actions",
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
                            modifier = Modifier.size(32.dp),
                            tint = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Bus Arrivals",
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
                            modifier = Modifier.size(32.dp),
                            tint = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "MRT Stations",
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

@Composable
private fun ServiceStatusCard(trainAlerts: List<TrainAlert>) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (trainAlerts.isNotEmpty()) MinimalistColors.PrimarySurface else MinimalistColors.SecondarySurface
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
                    imageVector = if (trainAlerts.isNotEmpty()) Icons.Default.Warning else Icons.Default.CheckCircle,
                    contentDescription = null,
                    tint = if (trainAlerts.isNotEmpty()) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = "Service Status",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.SemiBold
                    ),
                    color = MinimalistColors.PrimaryText
                )
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            if (trainAlerts.isEmpty()) {
                Text(
                    text = "All services running normally",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MinimalistColors.PrimaryText.copy(alpha = 0.7f)
                )
            } else {
                Text(
                    text = "${trainAlerts.size} service alert(s)",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.error
                )
                trainAlerts.take(1).forEach { alert ->
                    Spacer(modifier = Modifier.height(8.dp))
                    Card(
                        modifier = Modifier.fillMaxWidth(),
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
private fun TransportSummaryCard(
    busStopsCount: Int,
    mrtStationsCount: Int,
    navController: NavHostController
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
            Text(
                text = "Nearby Transport",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.SemiBold
                ),
                color = MinimalistColors.PrimaryText,
                modifier = Modifier.padding(bottom = 16.dp)
            )
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                TransportCountCard(
                    icon = Icons.Default.DirectionsBus,
                    count = busStopsCount,
                    label = "Bus Stops",
                    onClick = { navController.navigate(NavigationRoutes.BusArrivals.route) },
                    modifier = Modifier.weight(1f)
                )
                
                TransportCountCard(
                    icon = Icons.Default.Train,
                    count = mrtStationsCount,
                    label = "MRT Stations",
                    onClick = { navController.navigate(NavigationRoutes.MrtStations.route) },
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
private fun TransportCountCard(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    count: Int,
    label: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        onClick = onClick,
        modifier = modifier,
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
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.size(24.dp),
                tint = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = count.toString(),
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Bold
                ),
                color = MinimalistColors.PrimaryText
            )
            Text(
                text = label,
                style = MaterialTheme.typography.labelMedium,
                color = MinimalistColors.PrimaryText.copy(alpha = 0.7f),
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
private fun NearbyHighlightsCard(
    travelInfo: SingaporeTravelInfo,
    navController: NavHostController
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
                Text(
                    text = "Nearby Highlights",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.SemiBold
                    ),
                    color = MinimalistColors.PrimaryText
                )
                
                TextButton(onClick = { navController.navigate(NavigationRoutes.BusArrivals.route) }) {
                    Text("View All")
                }
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            if (travelInfo.nearbyBusStops.isNotEmpty()) {
                val closestBusStop = travelInfo.nearbyBusStops.first()
                HighlightItem(
                    icon = Icons.Default.DirectionsBus,
                    title = closestBusStop.busStop.description,
                    subtitle = "Closest bus stop • ${closestBusStop.distance?.let { "${(it * 10).toInt() / 10.0} km" } ?: ""}",
                    onClick = { navController.navigate("travel/bus/${closestBusStop.busStop.busStopCode}") }
                )
            }
            
            if (travelInfo.nearbyMrtStations.isNotEmpty()) {
                Spacer(modifier = Modifier.height(8.dp))
                val closestMrtStation = travelInfo.nearbyMrtStations.first()
                HighlightItem(
                    icon = Icons.Default.Train,
                    title = closestMrtStation.station.stationName,
                    subtitle = "Closest MRT station • ${closestMrtStation.distance?.let { "${(it * 10).toInt() / 10.0} km" } ?: ""}",
                    onClick = { navController.navigate("travel/mrt/${closestMrtStation.station.stationCode}") }
                )
            }
            
            if (travelInfo.nearbyBusStops.isEmpty() && travelInfo.nearbyMrtStations.isEmpty()) {
                Text(
                    text = "No nearby transport found",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MinimalistColors.PrimaryText.copy(alpha = 0.7f)
                )
            }
        }
    }
}

@Composable
private fun HighlightItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    subtitle: String,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MinimalistColors.PrimarySurface
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontWeight = FontWeight.SemiBold
                    ),
                    color = MinimalistColors.PrimaryText
                )
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = MinimalistColors.PrimaryText.copy(alpha = 0.7f)
                )
            }
            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = "View details",
                tint = MinimalistColors.PrimaryText.copy(alpha = 0.5f),
                modifier = Modifier.size(16.dp)
            )
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

private fun formatLastUpdated(lastUpdated: String): String {
    return try {
        val instant = kotlinx.datetime.Instant.parse(lastUpdated)
        val localDateTime = instant.toLocalDateTime(kotlinx.datetime.TimeZone.currentSystemDefault())
        "${localDateTime.hour.toString().padStart(2, '0')}:${localDateTime.minute.toString().padStart(2, '0')}"
    } catch (e: Exception) {
        "Unknown"
    }
}