package com.uitopic.restockmobile.features.planning.domain.models

data class RecipeSupply(
    val supplyId: Int,
    val quantity: Double,
    val supplyName: String? = null,  // Para mostrar en UI (se obtiene del CustomSupply)
    val unitName: String? = null      // Unidad de medida
)