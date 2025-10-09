package com.uitopic.restockmobile.features.planning.domain.models

data class UpdateRecipeSupplyRequest(
    val supplyId: Int,
    val quantity: Double
)