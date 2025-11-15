package com.uitopic.restockmobile.features.resources.inventory.domain.models

data class Batch(
    val id: String,
    val userId: Int?,
    val userRoleId: Int?,
    val customSupply: CustomSupply?,
    val stock: Double,
    val expirationDate: String?
)
