package com.cherry.kmp.common

interface Platform {
    val name: String
    val isAndroid: Boolean
    val isIos: Boolean
        get() = !isAndroid
}

expect fun getPlatform(): Platform