package com.cherry.kmp.ui.main

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import com.cherry.kmp.domain.UiState
import com.cherry.kmp.domain.model.Article
import com.cherry.kmp.ui.component.ArticleView
import com.cherry.kmp.ui.component.ErrorScreen
import com.cherry.kmp.ui.component.LoadingScreen
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
    Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
        articles.forEach { article ->
            ArticleView(article)
        }
    }
}