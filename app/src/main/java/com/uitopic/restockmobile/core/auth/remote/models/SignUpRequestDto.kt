package com.uitopic.restockmobile.core.auth.remote.models

import com.google.gson.annotations.SerializedName

data class SignUpRequestDto(
    @SerializedName("username") val username: String,
    @SerializedName("password") val password: String,
    @SerializedName("roleId") val roleId: Int
)
