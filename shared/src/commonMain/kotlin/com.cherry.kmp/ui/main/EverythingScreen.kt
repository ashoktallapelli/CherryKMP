package com.cherry.kmp.ui.main

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalUriHandler
import androidx.navigation.NavHostController
import cherrykmp.shared.generated.resources.Res
import cherrykmp.shared.generated.resources.all_news
import com.cherry.kmp.domain.UiState
import com.cherry.kmp.domain.model.Article
import com.cherry.kmp.ui.component.ArticleView
import com.cherry.kmp.ui.component.ErrorScreen
import com.cherry.kmp.ui.component.LoadingScreen
import com.cherry.kmp.ui.component.MyToolbar
import com.cherry.kmp.ui.main.viewmodel.MainViewModel
import com.cherry.kmp.ui.navigation.navigateToArticle
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.koinInject

@Composable
internal fun EverythingScreen(
    viewModel: MainViewModel = koinInject(),
    navController: NavHostController
) {
    val news by viewModel.newsEverythingUiState.collectAsState()

    LaunchedEffect(key1 = Unit) {
        viewModel.loadEverythingNews()
    }

    Scaffold(topBar = {
        MyToolbar(
            title = stringResource(Res.string.all_news),
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
                when (val state = news) {
                    is UiState.Initial -> {}
                    is UiState.Loading -> {
                        LoadingScreen()
                    }

                    is UiState.Success -> {
                        ItemList(state.data.articles, navController)
                    }

                    is UiState.CachedSuccess -> {
                        ItemList(state.data.articles, navController)
                    }

                    is UiState.Error -> {
                        ErrorScreen(state.apiError.message.orEmpty()) {
                            viewModel.loadEverythingNews()
                        }
                    }
                }
            }
        })
}

@Composable
private fun ItemList(articles: List<Article>, navController: NavHostController) {
    val uriHandler = LocalUriHandler.current
    LazyColumn {
        items(
            items = articles,
            key = { article -> article.url ?: article.title ?: article.hashCode() }
        ) { article ->
            ArticleView(article) {
                // Try to navigate to article detail if we have an ID-like identifier
                // For demo purposes, we'll extract a number from the URL or use a hash
                val articleId = extractArticleId(article)
                if (articleId != null) {
                    navController.navigateToArticle(articleId)
                } else {
                    // Fallback to opening URL
                    article.url?.let {
                        uriHandler.openUri(it)
                    }
                }
            }
        }
    }
}

/**
 * Extract or generate an article ID for demonstration purposes
 * In a real app, this would come from your API data
 */
private fun extractArticleId(article: Article): Int? {
    return try {
        // Try to extract ID from URL path
        article.url?.let { url ->
            val pathSegments = url.split("/")
            pathSegments.lastOrNull()?.toIntOrNull()
        } ?: run {
            // Generate a stable ID from article hash for demo
            val hash = article.hashCode()
            kotlin.math.abs(hash % 100) + 1 // Generate ID between 1-100
        }
    } catch (e: Exception) {
        // Fallback ID for demo
        kotlin.math.abs(article.hashCode() % 100) + 1
    }
}