package com.uitopic.restockmobile.features.resources.orders.domain.models

import com.uitopic.restockmobile.features.auth.domain.models.User


data class Order(
    val id: Int,
    val adminRestaurantId: Int,
    val supplierId: Int,
    val supplier: User,
    val requestedDate: String = "",
    val partiallyAccepted: Boolean = false,
    val requestedProductsCount: Int = 0,
    val totalPrice: Double = 0.0,
    val state: OrderState = OrderState.ON_HOLD,
    val situation: OrderSituation = OrderSituation.PENDING,
    val batchItems: List<OrderBatchItem> = emptyList(),
    val description: String? = null,
    val estimatedShipDate: String? = null,
    val estimatedShipTime: String? = null
)