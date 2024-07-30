package com.cherry.kmp.ui.main

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalUriHandler
import androidx.navigation.NavHostController
import com.cherry.kmp.domain.UiState
import com.cherry.kmp.domain.model.Article
import com.cherry.kmp.ui.component.ArticleView
import com.cherry.kmp.ui.component.ErrorScreen
import com.cherry.kmp.ui.component.LoadingScreen
import com.cherry.kmp.ui.main.viewmodel.MainViewModel
import org.koin.compose.koinInject

@Composable
internal fun HeadlinesScreen(
    viewModel: MainViewModel = koinInject(),
    navController: NavHostController
) {
    val uiState = viewModel.newsHeadlines

    LaunchedEffect(key1 = Unit) {
        viewModel.loadHeadlinesNews()
    }

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

            is UiState.Error -> {
                ErrorScreen(state.apiError.message.orEmpty()) {
                    viewModel.loadHeadlinesNews()
                }
            }
        }
    }
}

@Composable
private fun ItemList(articles: List<Article>) {
    val uriHandler = LocalUriHandler.current
    LazyColumn {
        items(articles) { article ->
            ArticleView(article) {
                article.url?.let {
                    uriHandler.openUri(it)
                }
            }
        }
    }
}