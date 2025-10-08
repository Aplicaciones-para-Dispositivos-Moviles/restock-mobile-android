package com.uitopic.restockmobile.features.resources.data.local.mappers
import com.uitopic.restockmobile.features.resources.data.local.models.BatchEntity
import com.uitopic.restockmobile.features.resources.domain.models.Batch

fun BatchEntity.toDomain(): Batch =
    Batch(
        id = this.id,
        userId = this.userId,
        customSupply = null,
        stock = this.stock,
        expirationDate = this.expirationDate
    )
fun com.uitopic.restockmobile.features.resources.domain.models.Batch.toEntity(customSupplyId: String? = null): BatchEntity =
    BatchEntity(
        id = this.id,
        userId = this.userId,
        customSupplyId = customSupplyId ?: this.customSupply?.id ?: "",
        stock = this.stock,
        expirationDate = this.expirationDate
    )