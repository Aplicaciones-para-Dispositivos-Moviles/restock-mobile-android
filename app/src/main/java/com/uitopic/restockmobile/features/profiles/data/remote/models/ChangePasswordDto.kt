package com.uitopic.restockmobile.features.profiles.data.remote.models

import com.google.gson.annotations.SerializedName

data class ChangePasswordDto(
    @SerializedName("currentPassword") val currentPassword: String,
    @SerializedName("newPassword") val newPassword: String
)
