package com.cherry.kmp.domain.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Post(
    @SerialName("userId")
    var userId: Int,
    @SerialName("id")
    var id: Int,
    @SerialName("title")
    var title: String,
    @SerialName("body")
    var body: String
)