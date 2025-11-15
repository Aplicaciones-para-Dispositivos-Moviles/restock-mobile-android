package com.uitopic.restockmobile.features.profiles.data.remote.models

import com.google.gson.annotations.SerializedName

data class UpdateBusinessDataDto(
    @SerializedName("businessName") val businessName: String,
    @SerializedName("businessAddress") val businessAddress: String,
    @SerializedName("description") val description: String?,
    @SerializedName("businessCategoryIds") val businessCategoryIds: List<String>
)
