package com.cherry.kmp.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import com.cherry.kmp.domain.model.Article

@Composable
fun ArticleView(article: Article, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(4.dp)
            .background(
                color = MaterialTheme.colorScheme.surfaceContainer,
                shape = RoundedCornerShape(8.dp)
            )
            .clickable {
                onClick()
            }
    ) {
        Column(modifier = Modifier.padding(all = 4.dp)) {
            AsyncImage(
                model = article.urlToImage,
                contentDescription = article.author,
                modifier = Modifier.padding(10.dp),
                contentScale = ContentScale.Crop
            )
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