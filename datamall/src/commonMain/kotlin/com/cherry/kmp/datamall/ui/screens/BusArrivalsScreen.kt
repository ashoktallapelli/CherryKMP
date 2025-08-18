package com.cherry.kmp.datamall.ui.screens

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
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
import com.cherry.kmp.datamall.navigation.DataMallNavigationContract
import com.cherry.kmp.core.domain.UiState
import com.cherry.kmp.datamall.domain.model.*
import com.cherry.kmp.core.ui.component.ErrorScreen
import com.cherry.kmp.core.ui.component.LoadingScreen
import com.cherry.kmp.datamall.ui.viewmodel.SingaporeTravelViewModel
import com.cherry.kmp.core.ui.theme.MinimalistColors
import kotlinx.datetime.Clock
import org.koin.compose.koinInject
import kotlinx.datetime.Instant

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BusArrivalsScreen(
    viewModel: SingaporeTravelViewModel = koinInject(),
    navigation: DataMallNavigationContract = koinInject()
) {
    var isScreenLoaded by remember { mutableStateOf(false) }
    
    val busStopsState by viewModel.busStopsState.collectAsState()
    val listState = rememberLazyListState()
    
    LaunchedEffect(Unit) {
        isScreenLoaded = true
        val singaporeLocation = LocationData(latitude = 1.3521, longitude = 103.8198)
        viewModel.loadNearbyBusStops(singaporeLocation)
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
            CollapsiBusArrivalsHeader(
                scrollProgress = scrollProgress,
                onBackClick = { navigation.navigateBack() }
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .scale(screenAnimationScale)
        ) {
            when (val state = busStopsState) {
                is UiState.Loading -> LoadingScreen()
                is UiState.Success -> {
                    BusArrivalsContent(
                        busStops = state.data,
                        listState = listState,
                        scrollProgress = scrollProgress,
                        onBusStopClick = { busStop ->
                            navigation.navigateToBusDetail(busStop.busStopCode)
                        }
                    )
                }
                is UiState.Error -> {
                    ErrorScreen(
                        errorMessage = state.apiError.message ?: "Unable to load bus arrivals",
                        onRetry = { 
                            val singaporeLocation = LocationData(latitude = 1.3521, longitude = 103.8198)
                            viewModel.loadNearbyBusStops(singaporeLocation)
                        }
                    )
                }
                is UiState.CachedSuccess -> {
                    BusArrivalsContent(
                        busStops = state.data,
                        listState = listState,
                        scrollProgress = scrollProgress,
                        onBusStopClick = { busStop ->
                            navigation.navigateToBusDetail(busStop.busStopCode)
                        }
                    )
                }
                is UiState.Initial -> LoadingScreen()
            }
        }
    }
}

@Composable
private fun CollapsiBusArrivalsHeader(
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
                                text = "🚌 Bus Arrivals",
                                style = MaterialTheme.typography.headlineSmall.copy(
                                    fontWeight = FontWeight.Bold
                                ),
                                color = MinimalistColors.PrimaryText
                            )
                            Text(
                                text = "Real-time bus arrival information",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MinimalistColors.SecondaryText.copy(alpha = 0.7f),
                                modifier = Modifier.alpha(1f - (scrollProgress * 0.3f))
                            )
                        }
                        
                        if (scrollProgress > 0.3f) {
                            Icon(
                                imageVector = Icons.Default.DirectionsBus,
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
private fun BusArrivalsContent(
    busStops: List<BusStopWithArrivals>,
    listState: LazyListState,
    scrollProgress: Float,
    onBusStopClick: (BusStop) -> Unit
) {
    LazyColumn(
        state = listState,
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Text(
                text = "Nearby Bus Stops",
                style = MaterialTheme.typography.headlineSmall.copy(
                    fontWeight = FontWeight.Bold
                ),
                color = MinimalistColors.PrimaryText,
                modifier = Modifier
                    .alpha(1f - scrollProgress)
                    .padding(vertical = 16.dp)
            )
        }
        
        if (busStops.isEmpty()) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MinimalistColors.SecondarySurface
                    ),
                    shape = RoundedCornerShape(20.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(40.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = Icons.Default.DirectionsBus,
                            contentDescription = null,
                            modifier = Modifier.size(64.dp),
                            tint = MinimalistColors.PrimaryText.copy(alpha = 0.5f)
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "No bus stops found",
                            style = MaterialTheme.typography.titleMedium,
                            color = MinimalistColors.PrimaryText
                        )
                        Text(
                            text = "Try refreshing or check your location",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MinimalistColors.SecondaryText,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        } else {
            items(busStops) { busStopWithArrivals ->
                BusStopCard(
                    busStopWithArrivals = busStopWithArrivals,
                    onBusStopClick = { onBusStopClick(busStopWithArrivals.busStop) }
                )
            }
        }
    }
}

@Composable
private fun BusStopCard(
    busStopWithArrivals: BusStopWithArrivals,
    onBusStopClick: () -> Unit
) {
    Card(
        onClick = onBusStopClick,
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
                verticalAlignment = Alignment.Top
            ) {
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = busStopWithArrivals.busStop.description,
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold
                        ),
                        color = MinimalistColors.PrimaryText
                    )
                    Text(
                        text = "Stop ${busStopWithArrivals.busStop.busStopCode} • ${busStopWithArrivals.busStop.roadName}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MinimalistColors.PrimaryText.copy(alpha = 0.7f)
                    )
                    busStopWithArrivals.distance?.let { distance ->
                        Text(
                            text = "${(distance * 10).toInt() / 10.0} km away",
                            style = MaterialTheme.typography.bodySmall,
                            color = MinimalistColors.PrimaryText.copy(alpha = 0.5f)
                        )
                    }
                }
                
                Icon(
                    imageVector = Icons.Default.ChevronRight,
                    contentDescription = "View details",
                    tint = MinimalistColors.PrimaryText.copy(alpha = 0.5f),
                    modifier = Modifier.size(24.dp)
                )
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            busStopWithArrivals.arrivals?.services?.let { services ->
                if (services.isEmpty()) {
                    Text(
                        text = "No bus services available",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MinimalistColors.PrimaryText.copy(alpha = 0.7f)
                    )
                } else {
                    Text(
                        text = "Bus Services (${services.size})",
                        style = MaterialTheme.typography.labelLarge.copy(
                            fontWeight = FontWeight.SemiBold
                        ),
                        color = MinimalistColors.PrimaryText,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(services.take(5)) { service ->
                            BusServiceChip(service = service)
                        }
                        if (services.size > 5) {
                            item {
                                Card(
                                    colors = CardDefaults.cardColors(
                                        containerColor = MinimalistColors.PrimarySurface
                                    ),
                                    shape = RoundedCornerShape(12.dp)
                                ) {
                                    Text(
                                        text = "+${services.size - 5} more",
                                        style = MaterialTheme.typography.labelMedium,
                                        color = MinimalistColors.PrimaryText,
                                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            } ?: run {
                Text(
                    text = "Unable to load arrival information",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MinimalistColors.PrimaryText.copy(alpha = 0.7f)
                )
            }
        }
    }
}

@Composable
private fun BusServiceChip(service: BusService) {
    val nextArrival = service.nextBus.estimatedArrival
    val arrivalTime = formatArrivalTime(nextArrival)
    val loadColor = when (service.nextBus.load.uppercase()) {
        "SEA" -> MaterialTheme.colorScheme.primary
        "SDA" -> MaterialTheme.colorScheme.tertiary
        "LSD" -> MaterialTheme.colorScheme.error
        else -> MinimalistColors.PrimaryText
    }
    
    Card(
        colors = CardDefaults.cardColors(
            containerColor = loadColor.copy(alpha = 0.2f)
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = service.serviceNo,
                style = MaterialTheme.typography.labelLarge.copy(
                    fontWeight = FontWeight.Bold
                ),
                color = loadColor
            )
            Text(
                text = arrivalTime,
                style = MaterialTheme.typography.labelMedium,
                color = loadColor.copy(alpha = 0.8f)
            )
        }
    }
}

private fun formatArrivalTime(estimatedArrival: String): String {
    return try {
        val arrivalInstant = Instant.parse(estimatedArrival)
        val currentInstant = Clock.System.now()
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