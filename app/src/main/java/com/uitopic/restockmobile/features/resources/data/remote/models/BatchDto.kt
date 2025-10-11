package com.uitopic.restockmobile.features.resources.data.remote.models

data class BatchDto(
    val id: String?,
    val userId: Int?,
    val customSupplyId: Int?,
    val stock: Int?,
    val expirationDate: String?
)
