package com.cherry.kmp

import androidx.compose.runtime.Composable
import com.cherry.kmp.ui.App
import com.cherry.kmp.ui.navigation.DeepLinkData

@Composable
fun MainView(deepLinkData: DeepLinkData? = null) {
    App(deepLinkData = deepLinkData)
}