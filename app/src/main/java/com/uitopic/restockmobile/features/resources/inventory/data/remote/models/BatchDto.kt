package com.uitopic.restockmobile.features.resources.inventory.data.remote.models

import CustomSupplyDto

data class BatchDto(
    val id: String?,
    val userId: Int?,
    val userRoleId: Int?,
    val customSupplyId: Int?,
    val stock: Double?,
    val expirationDate: String?,
    val customSupply: CustomSupplyDto? = null
)