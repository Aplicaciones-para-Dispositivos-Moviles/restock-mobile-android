package com.uitopic.restockmobile.features.planning.data.remote.models

import com.google.gson.annotations.SerializedName

data class RecipeDto(
    @SerializedName("id") val id: Int?,
    @SerializedName("name") val name: String?,
    @SerializedName("description") val description: String?,
    @SerializedName("imageUrl") val imageUrl: String?,
    @SerializedName("price") val price: Double?,
    @SerializedName("userId") val userId: Int?,
    @SerializedName("supplies") val supplies: List<RecipeSupplyDto>?
)
