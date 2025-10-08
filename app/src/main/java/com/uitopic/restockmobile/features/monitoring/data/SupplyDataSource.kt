package com.uitopic.restockmobile.features.monitoring.data

import com.uitopic.restockmobile.features.monitoring.domain.model.SupplyOption

object SupplyDataSource {
    fun getSupplyOptions(): List<SupplyOption> {
        return listOf(
            SupplyOption(1, "Lemon", "Fresh whole lemons", 2.50),
            SupplyOption(2, "Feta cheese", "Crumbled, 1 lb bag", 4.75),
            SupplyOption(3, "Olive oil", "Extra virgin 500 ml", 7.80),
            SupplyOption(4, "Pasta", "Rigatoni, 1 lb box", 3.60),
            SupplyOption(5, "Flour", "00 flour, 1 kg", 2.90)
        )
    }
}

