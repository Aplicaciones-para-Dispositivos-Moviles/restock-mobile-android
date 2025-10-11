package com.uitopic.restockmobile.features.resources.data.remote.models

data class BatchDto(
    val id: String?,
    val userId: Int?,
    val custom_supply: CustomSupplyDto?,
    val stock: Int?,
    val expiration_date: String?
)
