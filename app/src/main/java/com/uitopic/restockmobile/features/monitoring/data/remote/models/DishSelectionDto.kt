package com.uitopic.restockmobile.features.monitoring.data.remote.models

import com.google.gson.annotations.SerializedName

data class DishSelectionDto(
    @SerializedName("dishId")
    val dishId: Int,
    @SerializedName("quantity")
    val quantity: Int,
    @SerializedName("unitPrice")
    val unitPrice: Double
)

