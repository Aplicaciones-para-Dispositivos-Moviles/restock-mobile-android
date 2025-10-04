package com.uitopic.restockmobile.features.profiles.data.remote.models

import com.google.gson.annotations.SerializedName

data class ProfileDto(
    @SerializedName("userId") val userId: Int,
    @SerializedName("firstName") val firstName: String?,
    @SerializedName("lastName") val lastName: String?,
    @SerializedName("email") val email: String?,
    @SerializedName("phone") val phone: String?,
    @SerializedName("address") val address: String?,
    @SerializedName("country") val country: String?,
    @SerializedName("avatar") val avatar: String?,
    @SerializedName("businessName") val businessName: String?,
    @SerializedName("businessAddress") val businessAddress: String?,
    @SerializedName("description") val description: String?,
    @SerializedName("businessCategories") val businessCategories: List<CategoryDto>?
)