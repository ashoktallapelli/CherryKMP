package com.cherry.kmp.common

actual object Clock {
    actual fun currentTimeMillis(): Long = System.currentTimeMillis()
}