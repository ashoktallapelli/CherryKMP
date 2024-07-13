package com.guava.cherrykmp.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.guava.cherrykmp.data.model.DataModel

@Entity(tableName = "data_table")
data class DataModelEntity(
    @PrimaryKey val id: Int,
    val name: String
)

fun DataModelEntity.asExternalModel() = DataModel(
    id = id,
    name = name
)