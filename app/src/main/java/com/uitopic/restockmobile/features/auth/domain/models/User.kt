package com.uitopic.restockmobile.features.auth.domain.models

import com.google.gson.annotations.SerializedName

data class User(
    @SerializedName("id")
    val id: Int,
    @SerializedName("username")
    val username: String,
    @SerializedName("roleId")
    val roleId: Int,
    @SerializedName("subscription")
    val subscription: Int
)
