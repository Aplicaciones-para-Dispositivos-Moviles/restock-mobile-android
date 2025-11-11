package com.uitopic.restockmobile.features.resources.data.remote.models

import CustomSupplyDto

data class BatchDto(
    val id: String?,
    val userId: Int?,
    val customSupplyId: Int?,
    val stock: Double?,
    val expirationDate: String?,
    val customSupply: CustomSupplyDto? = null
)