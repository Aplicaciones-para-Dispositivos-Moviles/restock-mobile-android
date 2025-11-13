package com.uitopic.restockmobile.features.monitoring.data.remote.models

import com.google.gson.annotations.SerializedName

data class SupplySelectionDto(
    @SerializedName("supplyId")
    val supplyId: Int,
    @SerializedName("quantity")
    val quantity: Int,
    @SerializedName("unitPrice")
    val unitPrice: Double
)

