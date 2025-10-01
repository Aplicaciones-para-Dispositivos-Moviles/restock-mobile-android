package com.uitopic.restockmobile.features.profiles.domain.models

data class UpdateBusinessDataRequest(
    val businessName: String,
    val businessAddress: String,
    val categoryIds: List<String>
)