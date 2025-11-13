package com.uitopic.restockmobile.features.monitoring.data.remote.models

import com.google.gson.annotations.SerializedName

data class SaleDto(
    @SerializedName("id")
    val id: Int,
    @SerializedName("saleNumber")
    val saleNumber: String?,
    @SerializedName("dishSelections")
    val dishSelections: List<DishSelectionResponseDto>?,
    @SerializedName("supplySelections")
    val supplySelections: List<SupplySelectionResponseDto>?,
    @SerializedName("subtotal")
    val subtotal: Double?,
    @SerializedName("taxes")
    val taxes: Double?,
    @SerializedName("totalCost")
    val totalCost: Double,
    @SerializedName("registeredDate")
    val registeredDate: String?,
    @SerializedName("status")
    val status: String? = "completed"
)

data class DishSelectionResponseDto(
    @SerializedName("dishId")
    val dishId: Int,
    @SerializedName("dishName")
    val dishName: String?,
    @SerializedName("quantity")
    val quantity: Int,
    @SerializedName("unitPrice")
    val unitPrice: Double
)

data class SupplySelectionResponseDto(
    @SerializedName("supplyId")
    val supplyId: Int,
    @SerializedName("supplyName")
    val supplyName: String?,
    @SerializedName("quantity")
    val quantity: Int,
    @SerializedName("unitPrice")
    val unitPrice: Double
)
