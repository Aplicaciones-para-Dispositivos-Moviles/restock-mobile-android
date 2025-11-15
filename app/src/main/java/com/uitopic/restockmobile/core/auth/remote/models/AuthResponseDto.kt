package com.uitopic.restockmobile.core.auth.remote.models

import com.google.gson.annotations.SerializedName

data class AuthResponseDto(
    @SerializedName("id") val id: Int,
    @SerializedName("username") val username: String,
    @SerializedName("roleId") val roleId: Int,
    @SerializedName("subscription") val subscription: Int,
    @SerializedName("token") val token: String? = null  // Solo en sign-in
)