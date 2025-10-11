package com.uitopic.restockmobile.features.resources.domain.models

//Batch class

data class Batch(
    val id: String,
    val userId: Int?,
    val customSupply: CustomSupply?,
    val stock: Int,
    val expirationDate: String?
)
