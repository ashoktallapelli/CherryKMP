package com.cherry.kmp

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.text.font.FontWeight
import com.cherry.kmp.theme.AppTheme
import com.cherry.kmp.theme.grey_700

@Composable
internal fun App() {

//    KoinApplication(application = {
//        modules(platformModule)
//    }) {
    AppTheme {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Test",
                style = MaterialTheme.typography.displayLarge,
                color = grey_700,
                fontWeight = FontWeight.Bold
            )
        }
    }

//    })


}