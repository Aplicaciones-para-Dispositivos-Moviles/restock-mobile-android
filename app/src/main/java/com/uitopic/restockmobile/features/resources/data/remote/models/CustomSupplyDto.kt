package com.uitopic.restockmobile.features.resources.data.remote.models

data class CustomSupplyDto(
    val _id: String?,
    val min_stock: Int?,
    val max_stock: Int?,
    val price: Double?,
    val user_id: String?,
    val supply: SupplyDto?,
    val unit: UnitDto?
)
