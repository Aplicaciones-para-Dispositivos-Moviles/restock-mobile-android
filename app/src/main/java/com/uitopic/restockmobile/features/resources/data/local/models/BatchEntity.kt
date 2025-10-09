package com.uitopic.restockmobile.features.resources.data.local.models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "batches")
data class BatchEntity(
    @PrimaryKey val id: String,
    val userId: String?,
    val customSupplyId: String,
    val stock: Int,
    val expirationDate: String?
)
