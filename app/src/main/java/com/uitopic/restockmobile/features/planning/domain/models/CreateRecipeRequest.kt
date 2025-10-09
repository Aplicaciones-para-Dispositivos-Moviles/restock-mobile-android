package com.uitopic.restockmobile.features.planning.domain.models

data class CreateRecipeRequest(
    val name: String,
    val description: String,
    val imageUrl: String?,
    val price: Double,
    val userId: Int
)