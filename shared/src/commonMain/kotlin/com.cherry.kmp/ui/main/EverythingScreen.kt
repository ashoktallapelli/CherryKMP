package com.cherry.kmp.ui.main

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.cherry.kmp.domain.UiState
import com.cherry.kmp.domain.model.Article
import com.cherry.kmp.ui.component.ErrorScreen
import com.cherry.kmp.ui.component.LoadingScreen
import org.koin.compose.koinInject

@Composable
internal fun EverythingScreen(
    viewModel: MainViewModel = koinInject(),
    navController: NavHostController
) {
    val news = viewModel.newsEverything

    LaunchedEffect(key1 = Unit) {
        viewModel.loadEverythingNews()
    }

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
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
}

@Composable
private fun ItemRow(article: Article) {
    Card(
        modifier = Modifier
            .padding(all = 10.dp)
            .fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(all = 10.dp)) {
            Text(
                article.title.orEmpty(),
                fontSize = 25.sp,
                fontWeight = FontWeight.W700,
                modifier = Modifier.padding(10.dp)
            )
            Text(
                article.description.orEmpty(),
                color = Color.Gray,
                modifier = Modifier.padding(10.dp)
            )
        }
    }
}

@Composable
private fun ItemList(articles: List<Article>) {
    Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
        articles.forEach { post ->
            ItemRow(post)
        }
    }
}