package com.uitopic.restockmobile.features.resources.orders.domain.models


data class Order(
    val adminRestaurantId: Int,
    val supplierId: Int,
    val requestedDate: String = "",
    val partiallyAccepted: Boolean = false,
    val requestedProductsCount: Int = 0,
    val totalPrice: Double = 0.0,
    val state: OrderState = OrderState.ON_HOLD,
    val situation: OrderSituation = OrderSituation.PENDING,
    val batchItems: List<OrderBatchItem> = emptyList()
)