package com.uitopic.restockmobile.features.resources.domain.models

data class Supply(
    val id: Int,
    val name: String,
    val description: String?,
    val perishable: Boolean,
    val category: String?
)
