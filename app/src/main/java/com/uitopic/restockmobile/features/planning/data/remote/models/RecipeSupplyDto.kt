package com.uitopic.restockmobile.features.planning.data.remote.models

import com.google.gson.annotations.SerializedName

data class RecipeSupplyDto(
    @SerializedName("supplyId") val supplyId: String?,
    @SerializedName("quantity") val quantity: Double?
)