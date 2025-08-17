package com.cherry.kmp.ui.splash

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cherrykmp.shared.generated.resources.Res
import cherrykmp.shared.generated.resources.sg_buddy_logo
import kotlinx.coroutines.delay
import org.jetbrains.compose.resources.painterResource

@Composable
internal fun SplashScreen(navigateToMain: () -> Unit, navigateToLogin: () -> Unit) {
    val scale = remember {
        Animatable(0F)
    }

    LaunchedEffect(true) {
        scale.animateTo(
            targetValue = 1.0F,
            animationSpec = tween(
                durationMillis = 800
            )
        )
        delay(2000L)
        navigateToMain()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                androidx.compose.ui.graphics.Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFFE8F5E8), // Light green
                        Color(0xFFFFFFFF)  // White
                    )
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // SG Buddy Logo
            Image(
                painter = painterResource(Res.drawable.sg_buddy_logo),
                contentDescription = "SG Buddy Logo",
                modifier = Modifier
                    .size(150.dp)
                    .scale(scale.value)
            )
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // App Name
            Text(
                text = "SG Buddy",
                fontSize = 36.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF2E2E2E),
                modifier = Modifier.scale(scale.value)
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // Tagline
            Text(
                text = "YOUR LOCAL COMPANION",
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = Color(0xFF666666),
                letterSpacing = 2.sp,
                modifier = Modifier.scale(scale.value)
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Subtitle
            Text(
                text = "EXPLORE SINGAPORE",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFFD32F2F), // Red color matching Singapore flag
                textAlign = TextAlign.Center,
                modifier = Modifier.scale(scale.value)
            )
        }
    }
}