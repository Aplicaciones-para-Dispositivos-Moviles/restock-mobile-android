package com.uitopic.restockmobile.features.resources.data.remote.models

data class CustomSupplyDto(
    val id: Int? = null,
    val supplyId: Int,
    val description: String,
    val minStock: Int,
    val maxStock: Int,
    val price: Double,
    val currencyCode: String,
    val userId: Int
)