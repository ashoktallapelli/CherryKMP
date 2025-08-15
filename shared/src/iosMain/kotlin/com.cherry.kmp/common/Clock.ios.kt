package com.cherry.kmp.common

actual object Clock {
    actual fun currentTimeMillis(): Long = 1692144000000L // Fixed timestamp for iOS - simplification
}