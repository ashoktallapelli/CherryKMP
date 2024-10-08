package com.cherry.kmp.ui.main

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalUriHandler
import androidx.navigation.NavHostController
import cherrykmp.shared.generated.resources.Res
import cherrykmp.shared.generated.resources.news_sources
import cherrykmp.shared.generated.resources.sources
import com.cherry.kmp.domain.Constants
import com.cherry.kmp.domain.UiState
import com.cherry.kmp.domain.model.Article
import com.cherry.kmp.ui.component.ArticleView
import com.cherry.kmp.ui.component.ErrorScreen
import com.cherry.kmp.ui.component.ExposedDropdownView
import com.cherry.kmp.ui.component.LoadingScreen
import com.cherry.kmp.ui.component.MyToolbar
import com.cherry.kmp.ui.main.viewmodel.MainViewModel
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.koinInject

@Composable
internal fun SourcesScreen(
    viewModel: MainViewModel = koinInject(), navController: NavHostController
) {
    val news = viewModel.newsEverything

    LaunchedEffect(key1 = Unit) {
        viewModel.loadEverythingNews()
    }
    Scaffold(topBar = {
        MyToolbar(title = stringResource(Res.string.sources),
            showNavigation = false,
            showEditIcon = false,
            onNavigationClick = {},
            onEditClick = {})
    }, content = {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            val items = Constants.SOURCE_LIST

            ExposedDropdownView(
                defaultItem = items[0].name.orEmpty(),
                items = items,
                label = stringResource(Res.string.news_sources)
            ) {
                viewModel.loadEverythingNews(viewModel.getEverythingRequestWithSource(it.id.orEmpty()))
            }

            when (val state = news.value) {
                is UiState.Initial -> {}
                is UiState.Loading -> {
                    LoadingScreen()
                }

                is UiState.Success -> {
                    ItemList(state.data.articles)
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