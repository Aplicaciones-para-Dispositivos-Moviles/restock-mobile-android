package com.uitopic.restockmobile.features.resources.inventory.data.remote.models

data class SupplyDto(
    val id: Int?,
    val name: String,
    val description: String?,
    val perishable: Boolean?,
    val category: String?
)

