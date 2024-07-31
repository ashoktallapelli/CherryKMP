package com.cherry.kmp.data.local.entity

import androidx.compose.ui.graphics.ImageBitmap
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.cherry.kmp.domain.model.UserProfile


@Entity(tableName = "user_profile")
data class UserProfileEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val email: String,
    val image: ImageBitmap? = null
)

fun UserProfileEntity.asExternalModel() = UserProfile(
    id = id,
    name = name,
    email = email,
    image = image
)

fun UserProfile.asEntity() = UserProfileEntity(
    name = name,
    email = email,
    image = image
)