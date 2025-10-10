package com.uitopic.restockmobile.features.resources.domain.models

//Main class that represent a supply
data class Supply(
    val id: String,
    val name: String,
    val description: String?,
    val perishable: Boolean,
    val category: String?
)
