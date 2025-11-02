package com.uitopic.restockmobile.features.resources.domain.models

data class CustomSupply(
    val id: String,
    val minStock: Int,
    val maxStock: Int,
    val price: Double,
    val userId: String?,
    val supply: Supply,
    val unit: UnitModel
)
