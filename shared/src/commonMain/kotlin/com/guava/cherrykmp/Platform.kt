package com.guava.cherrykmp

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform