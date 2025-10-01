package com.uitopic.restockmobile.core.cloudinary.models
import com.google.gson.annotations.SerializedName

data class CloudinaryUploadResponse(
    @SerializedName("secure_url") val secureUrl: String,
    @SerializedName("public_id") val publicId: String,
    @SerializedName("format") val format: String,
    @SerializedName("width") val width: Int,
    @SerializedName("height") val height: Int,
    @SerializedName("bytes") val bytes: Long,
    @SerializedName("created_at") val createdAt: String
)