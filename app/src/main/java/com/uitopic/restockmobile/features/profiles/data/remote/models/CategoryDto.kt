package com.uitopic.restockmobile.features.profiles.data.remote.models

import com.google.gson.annotations.SerializedName

data class CategoryDto(
    @SerializedName("_id") val id: String,
    @SerializedName("category_name") val name: String
)