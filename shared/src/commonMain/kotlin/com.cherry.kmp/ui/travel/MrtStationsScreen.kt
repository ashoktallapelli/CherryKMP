package com.cherry.kmp.ui.travel

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.navigation.NavHostController
import com.cherry.kmp.core.domain.UiState
import com.cherry.kmp.datamall.domain.model.*
import com.cherry.kmp.ui.component.ErrorScreen
import com.cherry.kmp.ui.component.LoadingScreen
import com.cherry.kmp.datamall.ui.viewmodel.SingaporeTravelViewModel
import com.cherry.kmp.ui.theme.MinimalistColors
import org.koin.compose.koinInject

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun MrtStationsScreen(
    viewModel: SingaporeTravelViewModel = koinInject(),
    navController: NavHostController
) {
    var isScreenLoaded by remember { mutableStateOf(false) }
    
    val mrtStationsState by viewModel.mrtStationsState.collectAsState()
    val listState = rememberLazyListState()
    
    LaunchedEffect(Unit) {
        isScreenLoaded = true
        val singaporeLocation = LocationData(latitude = 1.3521, longitude = 103.8198)
        viewModel.loadNearbyMrtStations(singaporeLocation)
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
            CollapsingMrtStationsHeader(
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
            when (val state = mrtStationsState) {
                is UiState.Loading -> LoadingScreen()
                is UiState.Success -> {
                    MrtStationsContent(
                        mrtStations = state.data,
                        listState = listState,
                        scrollProgress = scrollProgress,
                        onStationClick = { station ->
                            navController.navigate("travel/mrt/${station.stationCode}")
                        }
                    )
                }
                is UiState.Error -> {
                    ErrorScreen(
                        errorMessage = state.apiError.message ?: "Unable to load MRT stations",
                        onRetry = { 
                            val singaporeLocation = LocationData(latitude = 1.3521, longitude = 103.8198)
                            viewModel.loadNearbyMrtStations(singaporeLocation)
                        }
                    )
                }
                is UiState.CachedSuccess -> {
                    MrtStationsContent(
                        mrtStations = state.data,
                        listState = listState,
                        scrollProgress = scrollProgress,
                        onStationClick = { station ->
                            navController.navigate("travel/mrt/${station.stationCode}")
                        }
                    )
                }
                is UiState.Initial -> LoadingScreen()
            }
        }
    }
}

@Composable
private fun CollapsingMrtStationsHeader(
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
                                text = "🚇 MRT Stations",
                                style = MaterialTheme.typography.headlineSmall.copy(
                                    fontWeight = FontWeight.Bold
                                ),
                                color = MinimalistColors.PrimaryText
                            )
                            Text(
                                text = "Train stations and crowd levels",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MinimalistColors.SecondaryText.copy(alpha = 0.7f),
                                modifier = Modifier.alpha(1f - (scrollProgress * 0.3f))
                            )
                        }
                        
                        if (scrollProgress > 0.3f) {
                            Icon(
                                imageVector = Icons.Default.Train,
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
private fun MrtStationsContent(
    mrtStations: List<MrtStationWithInfo>,
    listState: androidx.compose.foundation.lazy.LazyListState,
    scrollProgress: Float,
    onStationClick: (MrtStation) -> Unit
) {
    LazyColumn(
        state = listState,
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Text(
                text = "Nearby MRT Stations",
                style = MaterialTheme.typography.headlineSmall.copy(
                    fontWeight = FontWeight.Bold
                ),
                color = MinimalistColors.PrimaryText,
                modifier = Modifier
                    .alpha(1f - scrollProgress)
                    .padding(vertical = 16.dp)
            )
        }
        
        if (mrtStations.isEmpty()) {
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
                            imageVector = Icons.Default.Train,
                            contentDescription = null,
                            modifier = Modifier.size(64.dp),
                            tint = MinimalistColors.PrimaryText.copy(alpha = 0.5f)
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "No MRT stations found",
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
            items(mrtStations) { mrtStationWithInfo ->
                MrtStationCard(
                    mrtStationWithInfo = mrtStationWithInfo,
                    onStationClick = { onStationClick(mrtStationWithInfo.station) }
                )
            }
        }
    }
}

@Composable
private fun MrtStationCard(
    mrtStationWithInfo: MrtStationWithInfo,
    onStationClick: () -> Unit
) {
    Card(
        onClick = onStationClick,
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
                        text = mrtStationWithInfo.station.stationName,
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold
                        ),
                        color = MinimalistColors.PrimaryText
                    )
                    Text(
                        text = "Station ${mrtStationWithInfo.station.stationCode}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MinimalistColors.PrimaryText.copy(alpha = 0.7f)
                    )
                    mrtStationWithInfo.distance?.let { distance ->
                        Text(
                            text = "${(distance * 10).toInt() / 10.0} km away",
                            style = MaterialTheme.typography.bodySmall,
                            color = MinimalistColors.PrimaryText.copy(alpha = 0.5f)
                        )
                    }
                }
                
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    mrtStationWithInfo.crowdLevel?.let { crowdLevel ->
                        val (crowdText, crowdColor, crowdIcon) = when (crowdLevel.crowdLevel.lowercase()) {
                            "l" -> Triple("Low Crowd", MaterialTheme.colorScheme.primary, Icons.Default.Person)
                            "m" -> Triple("Moderate Crowd", MaterialTheme.colorScheme.tertiary, Icons.Default.People)
                            "h" -> Triple("High Crowd", MaterialTheme.colorScheme.error, Icons.Default.Group)
                            else -> Triple("Unknown", MinimalistColors.PrimaryText, Icons.Default.HelpOutline)
                        }
                        
                        Card(
                            colors = CardDefaults.cardColors(
                                containerColor = crowdColor.copy(alpha = 0.2f)
                            ),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = crowdIcon,
                                    contentDescription = null,
                                    tint = crowdColor,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = crowdText.split(" ").first(), // Just show "Low", "Moderate", "High"
                                    style = MaterialTheme.typography.labelMedium,
                                    color = crowdColor
                                )
                            }
                        }
                    }
                    
                    Icon(
                        imageVector = Icons.Default.ChevronRight,
                        contentDescription = "View details",
                        tint = MinimalistColors.PrimaryText.copy(alpha = 0.5f),
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Station lines indicator
            Card(
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
                        imageVector = Icons.Default.Train,
                        contentDescription = "Train line",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = "MRT Station • Multiple Lines",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MinimalistColors.PrimaryText
                    )
                    Spacer(modifier = Modifier.weight(1f))
                    mrtStationWithInfo.crowdLevel?.let {
                        Text(
                            text = "Updated recently",
                            style = MaterialTheme.typography.labelSmall,
                            color = MinimalistColors.PrimaryText.copy(alpha = 0.7f)
                        )
                    }
                }
            }
        }
    }
}