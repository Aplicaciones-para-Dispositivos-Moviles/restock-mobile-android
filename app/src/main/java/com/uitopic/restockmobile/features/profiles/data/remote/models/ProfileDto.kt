package com.uitopic.restockmobile.features.profiles.data.remote.models

import com.google.gson.annotations.SerializedName

data class ProfileDto(
    @SerializedName("_id") val id: String,
    @SerializedName("first_name") val firstName: String,
    @SerializedName("last_name") val lastName: String,
    @SerializedName("email") val email: String,
    @SerializedName("phone") val phone: String,
    @SerializedName("address") val address: String,
    @SerializedName("country") val country: String,
    @SerializedName("avatar") val avatar: String?,
    @SerializedName("business_name") val businessName: String,
    @SerializedName("business_address") val businessAddress: String,
    @SerializedName("description") val description: String?,
    @SerializedName("categories") val categories: List<CategoryDto>
)