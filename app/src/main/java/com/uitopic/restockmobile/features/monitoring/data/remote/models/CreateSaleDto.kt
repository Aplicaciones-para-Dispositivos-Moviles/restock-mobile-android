package com.uitopic.restockmobile.features.monitoring.data.remote.models

import com.google.gson.annotations.SerializedName

data class CreateSaleDto(
    @SerializedName("dishSelections")
    val dishSelections: List<DishSelectionDto>,
    @SerializedName("supplySelections")
    val supplySelections: List<SupplySelectionDto>,
    @SerializedName("subtotal")
    val subtotal: Double,
    @SerializedName("taxes")
    val taxes: Double,
    @SerializedName("totalCost")
    val totalCost: Double,
    @SerializedName("userId")
    val userId: Int
)

