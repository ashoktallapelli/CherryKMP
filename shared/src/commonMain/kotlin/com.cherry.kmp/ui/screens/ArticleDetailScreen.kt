package com.cherry.kmp.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.cherry.kmp.domain.model.Post
import com.cherry.kmp.domain.usecase.GetPostsUseCase
import com.cherry.kmp.domain.UiState
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
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Article Details") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    when (val state = uiState) {
                        is UiState.Success -> {
                            state.data?.let { article ->
                                IconButton(
                                    onClick = { 
                                        // TODO: Implement sharing functionality
                                        // shareArticle(article)
                                    }
                                ) {
                                    Icon(Icons.Default.Share, contentDescription = "Share")
                                }
                            }
                        }
                        else -> { /* No share button for loading/error states */ }
                    }
                }
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when (val state = uiState) {
                is UiState.Loading -> {
                    LoadingScreen()
                }
                
                is UiState.Error -> {
                    ErrorScreen(
                        errorMessage = "Failed to load article",
                        onRetry = {
                            scope.launch {
                                uiState = UiState.Loading
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
private fun ArticleContent(article: Post) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Article metadata
        Card(
            modifier = Modifier.fillMaxWidth(),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "Article #${article.id}",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = "By User ${article.userId}",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
        
        // Article title
        Text(
            text = article.title,
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.fillMaxWidth()
        )
        
        Divider()
        
        // Article body
        Text(
            text = article.body,
            style = MaterialTheme.typography.bodyLarge,
            lineHeight = MaterialTheme.typography.bodyLarge.lineHeight * 1.4,
            modifier = Modifier.fillMaxWidth()
        )
        
        // Add some bottom spacing
        Spacer(modifier = Modifier.height(32.dp))
    }
}

@Composable
private fun ArticleNotFound(
    articleId: Int,
    onBackClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Article Not Found",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Text(
            text = "The article with ID $articleId could not be found.",
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        
        Spacer(modifier = Modifier.height(24.dp))
        
        Button(
            onClick = onBackClick,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Go Back")
        }
    }
}