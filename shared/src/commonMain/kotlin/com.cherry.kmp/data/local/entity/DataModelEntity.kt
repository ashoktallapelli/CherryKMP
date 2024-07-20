package com.cherry.kmp.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.cherry.kmp.domain.model.DataModel

@Entity(tableName = "data_table")
data class DataModelEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String
)

fun DataModelEntity.asExternalModel() = DataModel(
    id = id,
    name = name
)