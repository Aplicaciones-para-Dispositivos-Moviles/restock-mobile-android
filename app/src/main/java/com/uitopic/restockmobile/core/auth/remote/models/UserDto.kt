package com.uitopic.restockmobile.core.auth.remote.models

import com.google.gson.annotations.SerializedName
import com.uitopic.restockmobile.features.profiles.data.remote.models.ProfileDto

data class UserDto(
    @SerializedName("id") val id: Int,
    @SerializedName("username") val username: String,
    @SerializedName("roleId") val roleId: Int,
    @SerializedName("profile") val profile: ProfileDto?
)
