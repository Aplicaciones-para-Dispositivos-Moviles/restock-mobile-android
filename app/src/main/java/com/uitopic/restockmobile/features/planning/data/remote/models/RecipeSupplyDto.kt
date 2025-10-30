package com.uitopic.restockmobile.features.planning.data.remote.models

import com.google.gson.annotations.SerializedName

/**
 * RecipeSupply as returned by the API
 * Backend only returns supplyId (CustomSupply.id) and quantity
 * Supply name and unit data must be fetched separately from /custom-supplies
 */
data class RecipeSupplyDto(
    @SerializedName("supplyId") val supplyId: Int?,
    @SerializedName("quantity") val quantity: Double?
)