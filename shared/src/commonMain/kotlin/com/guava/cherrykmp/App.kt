package com.guava.cherrykmp

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.text.font.FontWeight
import com.guava.cherrykmp.di.platformModule
import com.guava.cherrykmp.theme.AppTheme
import com.guava.cherrykmp.theme.grey_700
import org.koin.compose.KoinApplication

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