package com.cherry.kmp.ui.main

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Article
import androidx.compose.material.icons.filled.NewReleases
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Source
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import com.cherry.kmp.ui.theme.MinimalistColors
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.lerp
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.layout.ContentScale
import androidx.navigation.NavHostController
import cherrykmp.shared.generated.resources.Res
import cherrykmp.shared.generated.resources.all_news
import cherrykmp.shared.generated.resources.headlines
import cherrykmp.shared.generated.resources.sources
import com.cherry.kmp.domain.UiState
import com.cherry.kmp.domain.model.Article
import com.cherry.kmp.domain.model.Source
import com.cherry.kmp.ui.component.ArticleView
import com.cherry.kmp.ui.component.ErrorScreen
import com.cherry.kmp.ui.component.LoadingScreen
import com.cherry.kmp.ui.component.MyToolbar
import com.cherry.kmp.ui.main.viewmodel.MainViewModel
import com.cherry.kmp.ui.navigation.navigateToArticle
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.koinInject

enum class NewsTab(
    val title: String,
    val icon: ImageVector,
    val description: String
) {
    EVERYTHING("All News", Icons.Default.Article, "Latest articles from all sources"),
    HEADLINES("Headlines", Icons.Default.TrendingUp, "Breaking news and top stories"),
    SOURCES("Sources", Icons.Default.Source, "News sources and publications")
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun NewsScreen(
    viewModel: MainViewModel = koinInject(),
    navController: NavHostController
) {
    var selectedTab by remember { mutableStateOf(NewsTab.EVERYTHING) }
    var isScreenLoaded by remember { mutableStateOf(false) }
    
    val everythingNews by viewModel.newsEverythingUiState.collectAsState()
    val headlinesNews by viewModel.newsHeadlinesUiState.collectAsState()
    
    // Scroll state for collapsing effect
    val listState = rememberLazyListState()
    
    LaunchedEffect(Unit) {
        isScreenLoaded = true
        // Load all data
        viewModel.loadEverythingNews()
    }
    
    val screenAnimationScale by animateFloatAsState(
        targetValue = if (isScreenLoaded) 1f else 0.95f,
        animationSpec = tween(
            durationMillis = 400,
            easing = FastOutSlowInEasing
        )
    )

    // Calculate scroll progress for collapsing effect
    val scrollProgress = remember(listState.firstVisibleItemIndex, listState.firstVisibleItemScrollOffset) {
        when {
            listState.firstVisibleItemIndex > 1 -> 1f
            listState.firstVisibleItemIndex == 1 -> (listState.firstVisibleItemScrollOffset / 300f).coerceIn(0f, 1f)
            else -> 0f
        }
    }

    Scaffold(
        topBar = {
            CollapsingNewsHeader(
                selectedTab = selectedTab,
                scrollProgress = scrollProgress,
                onTabSelected = { selectedTab = it }
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .scale(screenAnimationScale)
        ) {
            // Get current UI state based on selected tab
            val currentUiState = when (selectedTab) {
                NewsTab.EVERYTHING -> everythingNews
                NewsTab.HEADLINES -> headlinesNews
                NewsTab.SOURCES -> UiState.Success(emptyList<Article>()) // Empty for sources
            }
            
            NewsContentWithCollapsing(
                uiState = currentUiState,
                selectedTab = selectedTab,
                listState = listState,
                scrollProgress = scrollProgress,
                onTabSelected = { selectedTab = it },
                onArticleClick = { article ->
                    // Navigate to article detail - article might not have an id field
                    // For now, use a dummy navigation
                    navController.navigate("article/1")
                },
                onRetry = { 
                    when (selectedTab) {
                        NewsTab.EVERYTHING -> viewModel.loadEverythingNews()
                        NewsTab.HEADLINES -> viewModel.loadHeadlinesNews()
                        NewsTab.SOURCES -> { /* No retry for sources yet */ }
                    }
                }
            )
        }
    }
}

@Composable
private fun CollapsingNewsHeader(
    selectedTab: NewsTab,
    scrollProgress: Float,
    onTabSelected: (NewsTab) -> Unit
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
            // Main header row
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
                    // Left side - App branding and tab info
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(lerp(0.dp, 12.dp, scrollProgress))
                    ) {
                        Column {
                            Text(
                                text = "CherryKMP",
                                style = MaterialTheme.typography.headlineSmall.copy(
                                    fontWeight = FontWeight.Bold
                                ),
                                color = MinimalistColors.PrimaryText
                            )
                            Text(
                                text = if (scrollProgress > 0.5f) selectedTab.title else "Stay informed with latest news",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MinimalistColors.SecondaryText.copy(alpha = 0.7f),
                                modifier = Modifier.alpha(1f - (scrollProgress * 0.3f))
                            )
                        }
                        
                        // Tab icon appears when collapsed
                        if (scrollProgress > 0.3f) {
                            Icon(
                                imageVector = selectedTab.icon,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary.copy(alpha = scrollProgress),
                                modifier = Modifier
                                    .size(lerp(0.dp, 24.dp, scrollProgress))
                                    .alpha(scrollProgress)
                            )
                        }
                    }
                    
                    // Right side - Search action
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
                                imageVector = Icons.Default.Search,
                                contentDescription = "Search",
                                tint = MinimalistColors.PrimaryText.copy(alpha = 0.8f),
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                }
            }
            
            // Collapsed tab selection (appears when scrolled)
            if (scrollProgress > 0.7f) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp)
                        .padding(bottom = 16.dp)
                        .alpha(scrollProgress),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    NewsTab.values().forEach { tab ->
                        FilterChip(
                            selected = selectedTab == tab,
                            onClick = { onTabSelected(tab) },
                            label = {
                                Text(
                                    text = tab.title,
                                    style = MaterialTheme.typography.labelSmall
                                )
                            },
                            modifier = Modifier.weight(1f),
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = MinimalistColors.DeepCharcoal,
                                selectedLabelColor = MinimalistColors.InverseText,
                                containerColor = MinimalistColors.SecondarySurface,
                                labelColor = MinimalistColors.PrimaryText
                            )
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun NewsContentWithCollapsing(
    uiState: UiState<*>,
    selectedTab: NewsTab,
    listState: LazyListState,
    scrollProgress: Float,
    onTabSelected: (NewsTab) -> Unit,
    onArticleClick: (Article) -> Unit,
    onRetry: () -> Unit
) {
    when (uiState) {
        is UiState.Loading -> {
            LoadingScreen()
        }
        is UiState.Success -> {
            val data = uiState.data
            val articles = when (data) {
                is com.cherry.kmp.domain.model.NewsResults -> data.articles
                is List<*> -> data.filterIsInstance<Article>()
                else -> emptyList()
            }
            
            if (articles.isEmpty()) {
                if (selectedTab == NewsTab.SOURCES) {
                    EmptySourcesState()
                } else {
                    EmptyNewsState()
                }
            } else {
                LazyColumn(
                    state = listState,
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Header section (will be pushed up when scrolling)
                    item {
                        Column(
                            modifier = Modifier
                                .alpha(1f - scrollProgress)
                                .padding(bottom = 16.dp),
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            // Header Section with Description
                            NewsHeaderSection(selectedTab = selectedTab)
                            
                            // Tab Selection
                            NewsTabSelection(
                                selectedTab = selectedTab,
                                onTabSelected = onTabSelected
                            )
                        }
                    }
                    
                    // Spacer item for smooth transition
                    item {
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                    
                    // Articles
                    items(articles) { article ->
                        ElegantArticleCard(
                            article = article,
                            onClick = { onArticleClick(article) }
                        )
                    }
                }
            }
        }
        is UiState.Error -> {
            ErrorScreen(
                errorMessage = uiState.apiError.message ?: "Unknown error occurred",
                onRetry = onRetry
            )
        }
        is UiState.CachedSuccess -> {
            val data = uiState.data
            val articles = when (data) {
                is com.cherry.kmp.domain.model.NewsResults -> data.articles
                is List<*> -> data.filterIsInstance<Article>()
                else -> emptyList()
            }
            
            if (articles.isEmpty()) {
                if (selectedTab == NewsTab.SOURCES) {
                    EmptySourcesState()
                } else {
                    EmptyNewsState()
                }
            } else {
                LazyColumn(
                    state = listState,
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Header section (will be pushed up when scrolling)
                    item {
                        Column(
                            modifier = Modifier
                                .alpha(1f - scrollProgress)
                                .padding(bottom = 16.dp),
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            // Header Section with Description
                            NewsHeaderSection(selectedTab = selectedTab)
                            
                            // Tab Selection
                            NewsTabSelection(
                                selectedTab = selectedTab,
                                onTabSelected = onTabSelected
                            )
                        }
                    }
                    
                    // Spacer item for smooth transition
                    item {
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                    
                    // Articles
                    items(articles) { article ->
                        ElegantArticleCard(
                            article = article,
                            onClick = { onArticleClick(article) }
                        )
                    }
                }
            }
        }
        is UiState.Initial -> {
            LoadingScreen()
        }
    }
}

@Composable
private fun ElegantNewsHeader() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        colors = CardDefaults.cardColors(
            containerColor = MinimalistColors.PrimarySurface
        ),
        shape = RoundedCornerShape(
            bottomStart = 24.dp,
            bottomEnd = 24.dp
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.Transparent)
                .padding(horizontal = 24.dp, vertical = 20.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Left side - App branding
                Column {
                    Text(
                        text = "CherryKMP",
                        style = MaterialTheme.typography.headlineSmall.copy(
                            fontWeight = FontWeight.Bold
                        ),
                        color = MinimalistColors.PrimaryText
                    )
                    Text(
                        text = "Stay informed with latest news",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MinimalistColors.SecondaryText.copy(alpha = 0.7f)
                    )
                }
                
                // Right side - Search action
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
                            imageVector = Icons.Default.Search,
                            contentDescription = "Search",
                            tint = MinimalistColors.PrimaryText.copy(alpha = 0.8f),
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun NewsHeaderSection(selectedTab: NewsTab) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
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
                        imageVector = selectedTab.icon,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.8f),
                        modifier = Modifier.size(32.dp)
                    )
                    
                    Spacer(modifier = Modifier.width(16.dp))
                    
                    Column {
                        Text(
                            text = selectedTab.title,
                            style = MaterialTheme.typography.headlineSmall.copy(
                                fontWeight = FontWeight.Bold
                            ),
                            color = MinimalistColors.PrimaryText
                        )
                        
                        Text(
                            text = selectedTab.description,
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
private fun NewsTabSelection(
    selectedTab: NewsTab,
    onTabSelected: (NewsTab) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
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
                text = "Browse News",
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
                NewsTab.values().forEach { tab ->
                    FilterChip(
                        selected = selectedTab == tab,
                        onClick = { onTabSelected(tab) },
                        label = {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Icon(
                                    imageVector = tab.icon,
                                    contentDescription = null,
                                    modifier = Modifier.size(16.dp)
                                )
                                Text(
                                    text = tab.title,
                                    style = MaterialTheme.typography.labelMedium
                                )
                            }
                        },
                        modifier = Modifier.weight(1f),
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = MinimalistColors.DeepCharcoal,
                            selectedLabelColor = MinimalistColors.InverseText,
                            selectedLeadingIconColor = MinimalistColors.InverseText,
                            containerColor = MinimalistColors.SecondarySurface,
                            labelColor = MinimalistColors.PrimaryText
                        )
                    )
                }
            }
        }
    }
}

@Composable
private fun NewsContent(
    uiState: UiState<*>,
    onArticleClick: (Article) -> Unit,
    onRetry: () -> Unit
) {
    when (uiState) {
        is UiState.Loading -> {
            LoadingScreen()
        }
        is UiState.Success -> {
            val data = uiState.data
            val articles = when (data) {
                is com.cherry.kmp.domain.model.NewsResults -> data.articles
                is List<*> -> data.filterIsInstance<Article>()
                else -> emptyList()
            }
            
            if (articles.isEmpty()) {
                EmptyNewsState()
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(articles) { article ->
                        ElegantArticleCard(
                            article = article,
                            onClick = { onArticleClick(article) }
                        )
                    }
                }
            }
        }
        is UiState.Error -> {
            ErrorScreen(
                errorMessage = uiState.apiError.message ?: "Unknown error occurred",
                onRetry = onRetry
            )
        }
        is UiState.CachedSuccess -> {
            val data = uiState.data
            val articles = when (data) {
                is com.cherry.kmp.domain.model.NewsResults -> data.articles
                is List<*> -> data.filterIsInstance<Article>()
                else -> emptyList()
            }
            
            if (articles.isEmpty()) {
                EmptyNewsState()
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(articles) { article ->
                        ElegantArticleCard(
                            article = article,
                            onClick = { onArticleClick(article) }
                        )
                    }
                }
            }
        }
        is UiState.Initial -> {
            LoadingScreen()
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ElegantArticleCard(
    article: Article,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
        colors = CardDefaults.cardColors(
            containerColor = MinimalistColors.SecondarySurface
        ),
        shape = RoundedCornerShape(16.dp)
    ) {
        ArticleView(
            article = article,
            onClick = onClick
        )
    }
}


@Composable
private fun EmptyNewsState() {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        ),
        shape = RoundedCornerShape(20.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = Icons.Default.NewReleases,
                contentDescription = null,
                modifier = Modifier.size(48.dp),
                tint = MinimalistColors.PrimaryText
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Text(
                text = "No News Available",
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Bold
                ),
                color = MinimalistColors.PrimaryText,
                textAlign = TextAlign.Center
            )
            
            Text(
                text = "Check back later for the latest updates",
                style = MaterialTheme.typography.bodyMedium,
                color = MinimalistColors.PrimaryText.copy(alpha = 0.7f),
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(top = 8.dp)
            )
        }
    }
}

@Composable
private fun EmptySourcesState() {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        ),
        shape = RoundedCornerShape(20.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = Icons.Default.Source,
                contentDescription = null,
                modifier = Modifier.size(48.dp),
                tint = MinimalistColors.PrimaryText
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Text(
                text = "No Sources Available",
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Bold
                ),
                color = MinimalistColors.PrimaryText,
                textAlign = TextAlign.Center
            )
            
            Text(
                text = "Sources will appear here when available",
                style = MaterialTheme.typography.bodyMedium,
                color = MinimalistColors.PrimaryText.copy(alpha = 0.7f),
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(top = 8.dp)
            )
        }
    }
}