package com.cherry.kmp.ui.screens

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.cherry.kmp.domain.model.Post
import com.cherry.kmp.core.ui.theme.MinimalistColors
import com.cherry.kmp.domain.usecase.GetPostsUseCase
import com.cherry.kmp.core.domain.UiState
import com.cherry.kmp.ui.component.ErrorScreen
import com.cherry.kmp.ui.component.LoadingScreen
import kotlinx.coroutines.launch
import org.koin.compose.koinInject

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ArticleDetailScreen(
    articleId: Int,
    navController: NavController,
    getPostsUseCase: GetPostsUseCase = koinInject()
) {
    var uiState by remember { mutableStateOf<UiState<Post?>>(UiState.Loading) }
    val scope = rememberCoroutineScope()
    
    // Load article data
    LaunchedEffect(articleId) {
        scope.launch {
            uiState = UiState.Loading
            try {
                getPostsUseCase(Unit).collect { postsState ->
                    when (postsState) {
                        is UiState.Success -> {
                            val article = postsState.data.find { it.id == articleId }
                            uiState = UiState.Success(article)
                        }
                        is UiState.Error -> {
                            uiState = UiState.Error(postsState.apiError)
                        }
                        is UiState.Loading -> {
                            uiState = UiState.Loading
                        }
                        else -> {
                            uiState = UiState.Error(Exception("Unknown state"))
                        }
                    }
                }
            } catch (e: Exception) {
                uiState = UiState.Error(e)
            }
        }
    }
    
    val currentState = uiState
    val showShareButton = currentState is UiState.Success && currentState.data != null
    
    Scaffold(
        topBar = {
            ElegantArticleHeader(
                onBackClick = { navController.popBackStack() },
                onShareClick = {
                    // TODO: Implement sharing functionality
                },
                showShareButton = showShareButton
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when (val state = currentState) {
                is UiState.Loading -> {
                    LoadingScreen()
                }
                
                is UiState.Error -> {
                    ErrorScreen(
                        errorMessage = "Failed to load article",
                        onRetry = {
                            scope.launch {
                                uiState = UiState.Loading
                                try {
                                    getPostsUseCase(Unit).collect { postsState ->
                                        when (postsState) {
                                            is UiState.Success -> {
                                                val article = postsState.data.find { it.id == articleId }
                                                uiState = UiState.Success(article)
                                            }
                                            is UiState.Error -> uiState = UiState.Error(postsState.apiError)
                                            else -> {}
                                        }
                                    }
                                } catch (e: Exception) {
                                    uiState = UiState.Error(e)
                                }
                            }
                        }
                    )
                }
                
                is UiState.Success -> {
                    val article = state.data
                    if (article != null) {
                        ArticleContent(article = article)
                    } else {
                        ArticleNotFound(
                            articleId = articleId,
                            onBackClick = { navController.popBackStack() }
                        )
                    }
                }
                
                else -> {
                    ErrorScreen(
                        errorMessage = "Unknown state"
                    ) { /* Handle retry */ }
                }
            }
        }
    }
}

@Composable
private fun ElegantArticleHeader(
    onBackClick: () -> Unit,
    onShareClick: () -> Unit,
    showShareButton: Boolean
) {
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
                // Left side - Back button and title
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Card(
                        modifier = Modifier.size(48.dp),
                        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MinimalistColors.SecondarySurface
                        ),
                        shape = RoundedCornerShape(16.dp),
                        onClick = onBackClick
                    ) {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Back",
                                tint = MinimalistColors.PrimaryText.copy(alpha = 0.8f),
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                    
                    Column {
                        Text(
                            text = "Article Details",
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.Bold
                            ),
                            color = MinimalistColors.PrimaryText
                        )
                        Text(
                            text = "Read full article content",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MinimalistColors.PrimaryText.copy(alpha = 0.7f)
                        )
                    }
                }
                
                // Right side - Share button (if available)
                if (showShareButton) {
                    Card(
                        modifier = Modifier.size(48.dp),
                        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MinimalistColors.SecondarySurface
                        ),
                        shape = RoundedCornerShape(16.dp),
                        onClick = onShareClick
                    ) {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Share,
                                contentDescription = "Share",
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
private fun ArticleContent(article: Post) {
    var isContentLoaded by remember { mutableStateOf(false) }
    
    LaunchedEffect(article) {
        isContentLoaded = true
    }
    
    val contentAnimationScale by animateFloatAsState(
        targetValue = if (isContentLoaded) 1f else 0.95f,
        animationSpec = tween(
            durationMillis = 400,
            easing = FastOutSlowInEasing
        )
    )
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
            .scale(contentAnimationScale),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        // Article Header Card
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
                Column(
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Article Title
                    Text(
                        text = article.title,
                        style = MaterialTheme.typography.headlineMedium.copy(
                            fontWeight = FontWeight.Bold
                        ),
                        color = MinimalistColors.PrimaryText
                    )
                    
                    // Article Metadata
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(24.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Author Info
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Person,
                                contentDescription = null,
                                tint = MinimalistColors.PrimaryText.copy(alpha = 0.8f),
                                modifier = Modifier.size(16.dp)
                            )
                            Text(
                                text = "User ${article.userId}",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MinimalistColors.PrimaryText.copy(alpha = 0.8f)
                            )
                        }
                        
                        // Article ID
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.AccessTime,
                                contentDescription = null,
                                tint = MinimalistColors.PrimaryText.copy(alpha = 0.8f),
                                modifier = Modifier.size(16.dp)
                            )
                            Text(
                                text = "Article #${article.id}",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MinimalistColors.PrimaryText.copy(alpha = 0.8f)
                            )
                        }
                    }
                }
            }
        }
        
        // Article Content Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
            colors = CardDefaults.cardColors(
                containerColor = MinimalistColors.SecondarySurface
            ),
            shape = RoundedCornerShape(20.dp)
        ) {
            Column(
                modifier = Modifier.padding(24.dp)
            ) {
                Text(
                    text = "Article Content",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.SemiBold
                    ),
                    color = MinimalistColors.PrimaryText,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
                
                Text(
                    text = article.body,
                    style = MaterialTheme.typography.bodyLarge,
                    lineHeight = MaterialTheme.typography.bodyLarge.lineHeight * 1.5,
                    color = MinimalistColors.PrimaryText.copy(alpha = 0.9f),
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
        
        // Add some bottom spacing
        Spacer(modifier = Modifier.height(50.dp))
    }
}

@Composable
private fun ArticleNotFound(
    articleId: Int,
    onBackClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
        colors = CardDefaults.cardColors(
            containerColor = MinimalistColors.SecondarySurface
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
                imageVector = Icons.Default.Person,
                contentDescription = null,
                modifier = Modifier.size(48.dp),
                tint = MinimalistColors.PrimaryText
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Text(
                text = "Article Not Found",
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontWeight = FontWeight.Bold
                ),
                color = MinimalistColors.PrimaryText,
                textAlign = TextAlign.Center
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = "The article with ID $articleId could not be found.",
                style = MaterialTheme.typography.bodyLarge,
                textAlign = TextAlign.Center,
                color = MinimalistColors.PrimaryText.copy(alpha = 0.7f)
            )
            
            Spacer(modifier = Modifier.height(24.dp))
            
            FilledTonalButton(
                onClick = onBackClick,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Go Back")
            }
        }
    }
}