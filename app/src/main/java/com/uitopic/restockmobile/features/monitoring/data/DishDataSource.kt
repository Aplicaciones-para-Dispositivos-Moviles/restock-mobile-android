package com.uitopic.restockmobile.features.monitoring.data

import com.uitopic.restockmobile.features.monitoring.domain.model.DishOption

object DishDataSource {
    fun getDishOptions(): List<DishOption> {
        return listOf(
            DishOption("Lomo Saltado", 1, 25.90),
            DishOption("Aji de Gallina", 2, 22.50),
            DishOption("Ceviche", 3, 28.90),
            DishOption("Arroz con Pollo", 4, 18.90),
            DishOption("Seco de Res", 5, 24.90),
            DishOption("Anticuchos", 6, 16.50),
            DishOption("Papa a la Huancaina", 7, 12.90),
            DishOption("Tacu Tacu", 8, 19.90),
            DishOption("Rocoto Relleno", 9, 21.50)
        )
    }
}

