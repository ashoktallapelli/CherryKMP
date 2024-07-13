package com.cherry.kmp.ui.splash

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ImageSearch
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import com.cherry.kmp.ui.theme.PrimaryColor
import kotlinx.coroutines.delay

@Composable
internal fun SplashScreen(navigateToMain: () -> Unit, navigateToLogin: () -> Unit) {
    val scale = remember {
        Animatable(0F)
    }

    LaunchedEffect(true) {
        scale.animateTo(
            targetValue = 0.7F,
            animationSpec = tween(
                durationMillis = 500
            )
        )
        delay(1000L)
        navigateToMain()
    }


    Column(
        modifier = Modifier.background(PrimaryColor),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            imageVector = Icons.Filled.ImageSearch,
            contentDescription = null,
            modifier = Modifier.scale(scale.value)
        )
    }
}