package com.cherry.kmp.ui.component

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import coil3.compose.rememberAsyncImagePainter


@Composable
fun CircleImage(
    image: String,
    modifier: Modifier = Modifier.size(55.dp),
    width: Dp = 1.5.dp,
    color: Color = Color.White,
    onClick: () -> Unit,
) {
    Card(
        modifier = modifier,
        onClick = onClick,
        border = if (width == 0.dp) null else BorderStroke(width = width, color = color),
        shape = CircleShape,
        elevation = CardDefaults.cardElevation(0.dp)
    ) {
        Image(
            rememberAsyncImagePainter(image),
            null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )
    }
}


@Composable
fun CircleImage(
    image: ImageBitmap?,
    modifier: Modifier = Modifier.size(55.dp),
    width: Dp = 1.5.dp,
    color: Color = Color.White,
    onClick: () -> Unit,
) {
    Card(
        modifier = modifier,
        onClick = onClick,
        border = if (width == 0.dp) null else BorderStroke(width = width, color = color),
        shape = CircleShape,
        elevation = CardDefaults.cardElevation(0.dp)
    ) {
        if (image == null) {
            Image(
                rememberAsyncImagePainter(image),
                null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
        } else {
            Image(
                image, null, contentScale = ContentScale.Crop, modifier = Modifier.fillMaxSize()
            )
        }
    }
}
