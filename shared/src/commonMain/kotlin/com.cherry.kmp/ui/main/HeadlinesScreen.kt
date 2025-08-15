package com.cherry.kmp.ui.main

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import cherrykmp.shared.generated.resources.Res
import cherrykmp.shared.generated.resources.news_headlines
import com.cherry.kmp.domain.UiState
import com.cherry.kmp.domain.model.Article
import com.cherry.kmp.ui.component.ArticleView
import com.cherry.kmp.ui.component.ErrorScreen
import com.cherry.kmp.ui.component.LoadingScreen
import com.cherry.kmp.ui.component.MyToolbar
import com.cherry.kmp.ui.main.viewmodel.MainViewModel
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.koinInject

@Composable
internal fun HeadlinesScreen(
    viewModel: MainViewModel = koinInject(),
    navController: NavHostController
) {
    val uiState = viewModel.newsHeadlinesUiState

    LaunchedEffect(key1 = Unit) {
        viewModel.loadHeadlinesNews()
    }

    Scaffold(topBar = {
        MyToolbar(
            title = stringResource(Res.string.news_headlines),
            showNavigation = false,
            showEditIcon = false,
            onNavigationClick = {},
            onEditClick = {})
    },
        content = {
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                when (val state = uiState.value) {
                    is UiState.Initial -> {}
                    is UiState.Loading -> {
                        LoadingScreen()
                    }

                    is UiState.Success -> {
                        ItemList(state.data.articles)
                    }

                    is UiState.CachedSuccess -> {
                        // Show cached data with offline indicator
                        ItemList(state.data.articles, showOfflineIndicator = true)
                    }

                    is UiState.Error -> {
                        ErrorScreen(state.apiError.message.orEmpty()) {
                            viewModel.loadHeadlinesNews()
                        }
                    }
                }
            }
        })
}

@Composable
private fun ItemList(articles: List<Article>, showOfflineIndicator: Boolean = false) {
    val uriHandler = LocalUriHandler.current
    LazyColumn {
        if (showOfflineIndicator) {
            item {
                OfflineIndicator()
            }
        }
        items(articles) { article ->
            ArticleView(article) {
                article.url?.let {
                    uriHandler.openUri(it)
                }
            }
        }
    }
}

@Composable
private fun OfflineIndicator() {
    // Simple offline indicator
    androidx.compose.material3.Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        colors = androidx.compose.material3.CardDefaults.cardColors(
            containerColor = androidx.compose.material3.MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        androidx.compose.material3.Text(
            text = "📱 Showing cached content - No internet connection",
            modifier = Modifier.padding(12.dp),
            style = androidx.compose.material3.MaterialTheme.typography.bodySmall
        )
    }
}