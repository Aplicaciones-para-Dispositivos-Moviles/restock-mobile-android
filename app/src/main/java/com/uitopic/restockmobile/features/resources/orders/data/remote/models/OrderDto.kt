package com.uitopic.restockmobile.features.resources.orders.data.remote.models


import com.google.gson.annotations.SerializedName
import com.uitopic.restockmobile.core.auth.remote.models.UserDto
import com.uitopic.restockmobile.features.resources.inventory.data.remote.models.BatchDto

data class OrderDto(
    val id: Int?,
    val adminRestaurantId: Int?,
    val supplierId: Int?,
    val supplier: UserDto?,
    @SerializedName("date")
    val requestedDate: String?,
    val partiallyAccepted: Boolean?,
    val requestedProductsCount: Int?,
    val totalPrice: Double?,
    val state: String?,
    val situation: String?,
    val batchItems: List<OrderBatchItemDto>?,
    val description: String?,
    val estimatedShipDate: String?,
    val estimatedShipTime: String?
)

data class OrderBatchItemDto(
    val batchId: Int?,
    val quantity: Double?,
    val accepted: Boolean?,
    val batch: BatchDto?
)

data class OrderRequestDto(
    val adminRestaurantId: Int,
    val supplierId: Int,
    @SerializedName("date")
    val requestedDate: String,
    val partiallyAccepted: Boolean = false,
    val requestedProductsCount: Int,
    val totalPrice: Double,
    val state: String,
    val situation: String,
    val batches: List<OrderBatchItemRequestDto>,
    val description: String? = null,
    val estimatedShipDate: String? = null,
    val estimatedShipTime: String? = null
)

data class OrderBatchItemRequestDto(
    val batchId: Int,
    val quantity: Double,
    val accept: Boolean = false
)