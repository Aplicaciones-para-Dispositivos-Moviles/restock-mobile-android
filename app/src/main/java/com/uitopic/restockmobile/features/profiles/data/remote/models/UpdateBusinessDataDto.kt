package com.uitopic.restockmobile.features.profiles.data.remote.models

import com.google.gson.annotations.SerializedName

data class UpdateBusinessDataDto(
    @SerializedName("business_name") val businessName: String,
    @SerializedName("business_address") val businessAddress: String,
    @SerializedName("category_ids") val categoryIds: List<String>
)
