package com.cherry.kmp.common

import androidx.compose.ui.graphics.ImageBitmap

expect fun ImageBitmap.toBytes(): ByteArray
expect fun ByteArray.toImageBitmap(): ImageBitmap