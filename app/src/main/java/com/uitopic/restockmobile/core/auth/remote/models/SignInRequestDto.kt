package com.uitopic.restockmobile.core.auth.remote.models

import com.google.gson.annotations.SerializedName

data class SignInRequestDto(
    @SerializedName("username") val username: String,
    @SerializedName("password") val password: String
)
