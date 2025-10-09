package com.uitopic.restockmobile.features.resources.domain.models

data class Batch(
    val id: String,
    val userId: String?,
    val customSupply: CustomSupply?,
    val stock: Int,
    val expirationDate: String?
)
