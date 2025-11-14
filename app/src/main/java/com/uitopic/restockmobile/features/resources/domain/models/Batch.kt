package com.uitopic.restockmobile.features.resources.domain.models

import com.uitopic.restockmobile.features.auth.domain.models.User

data class Batch(
    val id: String,
    val userId: Int?,
    val customSupply: CustomSupply?,
    val stock: Double,
    val expirationDate: String?
)
