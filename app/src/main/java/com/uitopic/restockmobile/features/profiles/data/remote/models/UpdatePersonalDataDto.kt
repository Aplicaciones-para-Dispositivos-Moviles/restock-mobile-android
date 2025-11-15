package com.uitopic.restockmobile.features.profiles.data.remote.models

import com.google.gson.annotations.SerializedName

data class UpdatePersonalDataDto(
    @SerializedName("firstName") val firstName: String,
    @SerializedName("lastName") val lastName: String,
    @SerializedName("email") val email: String,
    @SerializedName("phone") val phone: String,
    @SerializedName("address") val address: String,
    @SerializedName("country") val country: String,
    @SerializedName("avatar") val avatar: String?
)
