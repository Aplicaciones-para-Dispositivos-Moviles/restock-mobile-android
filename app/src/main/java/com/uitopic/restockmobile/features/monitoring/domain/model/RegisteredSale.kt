package com.uitopic.restockmobile.features.monitoring.domain.model

import java.util.Date

data class RegisteredSale(
    val id: Int,
    val saleNumber: String,
    val dishSelections: List<DishSelection>,
    val supplySelections: List<SupplySelection>,
    val totalCost: Double,
    val registeredDate: Date
)

