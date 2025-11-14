package com.uitopic.restockmobile.features.resources.orders.domain.models

import com.uitopic.restockmobile.features.resources.domain.models.Batch

data class OrderBatchItem(
    val batchId: Int,
    val quantity: Double,
    val accepted: Boolean,
    val batch: Batch?
)
