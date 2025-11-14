package com.uitopic.restockmobile.features.auth.domain.models

import com.google.gson.annotations.SerializedName

import com.uitopic.restockmobile.features.profiles.domain.models.Profile

data class User(
    @SerializedName("id")
    val id: Int,
    @SerializedName("username")
    val username: String,
    @SerializedName("roleId")
    val roleId: Int,
    val token: String? = null,
    val profile: Profile? = null,
    @SerializedName("subscription")
    val subscription: Int
)
