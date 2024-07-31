package com.cherry.kmp.domain.model

import androidx.compose.ui.graphics.ImageBitmap

data class UserProfile(val id: Long, val name: String, val email: String, val image: ImageBitmap?)
