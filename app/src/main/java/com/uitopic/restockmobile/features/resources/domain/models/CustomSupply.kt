package com.uitopic.restockmobile.features.resources.domain.models

data class CustomSupply(
    val id: String,
    val minStock: Int,
    val maxStock: Int,
    val price: Double,
    val userId: Int?,
    val supplyId: Int,
    val supply: Supply? = null,
    val unit: UnitModel,
    val currencyCode: String,
    val description: String,
)
