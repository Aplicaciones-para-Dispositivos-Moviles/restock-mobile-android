package com.uitopic.restockmobile.features.profiles.data.remote.models

import com.google.gson.annotations.SerializedName

data class CategoryDto(
    @SerializedName("id") val id: String,
    @SerializedName("name") val name: String
)