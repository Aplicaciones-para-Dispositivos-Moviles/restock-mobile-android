package com.uitopic.restockmobile.features.resources.data.remote.models

data class BatchDto(
    val _id: String?,
    val user_id: String?,
    val custom_supply: CustomSupplyDto?,
    val stock: Int?,
    val expiration_date: String?
)
