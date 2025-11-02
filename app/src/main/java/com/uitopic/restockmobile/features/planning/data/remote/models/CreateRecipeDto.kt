package com.uitopic.restockmobile.features.planning.data.remote.models

import com.google.gson.annotations.SerializedName

data class CreateRecipeDto(
    @SerializedName("name") val name: String,
    @SerializedName("description") val description: String,
    @SerializedName("imageUrl") val imageUrl: String,
    @SerializedName("price") val price: Double,
    @SerializedName("userId") val userId: Int
)
