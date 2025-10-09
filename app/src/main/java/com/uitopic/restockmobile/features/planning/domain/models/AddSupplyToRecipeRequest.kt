package com.uitopic.restockmobile.features.planning.domain.models

data class AddSupplyToRecipeRequest(
    val supplyId: Int,
    val quantity: Double
)
