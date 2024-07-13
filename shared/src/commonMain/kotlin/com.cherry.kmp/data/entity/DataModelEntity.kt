package com.cherry.kmp.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.cherry.kmp.data.model.DataModel

@Entity(tableName = "data_table")
data class DataModelEntity(
    @PrimaryKey val id: Int,
    val name: String
)

fun DataModelEntity.asExternalModel() = DataModel(
    id = id,
    name = name
)