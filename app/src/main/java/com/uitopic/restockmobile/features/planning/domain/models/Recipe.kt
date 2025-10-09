package com.uitopic.restockmobile.features.planning.domain.models

data class Recipe(
    val id: Int,
    val name: String,
    val description: String,
    val imageUrl: String?,
    val price: Double,
    val userId: Int,
    val supplies: List<RecipeSupply>
)
