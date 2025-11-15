package com.uitopic.restockmobile.features.monitoring.data.remote.mappers

import com.uitopic.restockmobile.features.monitoring.data.remote.models.*
import com.uitopic.restockmobile.features.monitoring.domain.model.DishOption
import com.uitopic.restockmobile.features.monitoring.domain.model.DishSelection
import com.uitopic.restockmobile.features.monitoring.domain.model.RegisteredSale
import com.uitopic.restockmobile.features.monitoring.domain.model.SupplyOption
import com.uitopic.restockmobile.features.monitoring.domain.model.SupplySelection
import java.text.SimpleDateFormat
import java.util.*

// Mapper para convertir DishSelection a DishSelectionDto
fun DishSelection.toDto(): DishSelectionDto {
    return DishSelectionDto(
        dishId = option.id,
        quantity = quantity,
        unitPrice = option.price
    )
}

// Mapper para convertir SupplySelection a SupplySelectionDto
fun SupplySelection.toDto(): SupplySelectionDto {
    return SupplySelectionDto(
        supplyId = option.id,
        quantity = quantity,
        unitPrice = option.unitPrice
    )
}

// Mapper para convertir SaleDto a RegisteredSale
fun SaleDto.toDomain(): RegisteredSale {
    val dateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
    val date = try {
        registeredDate?.let { dateFormat.parse(it) } ?: Date()
    } catch (_: Exception) {
        Date()
    }

    // Convertir DishSelectionResponseDto a DishSelection
    val dishes = dishSelections?.map { dto ->
        DishSelection(
            option = DishOption(
                id = dto.dishId,
                label = dto.dishName ?: "Dish #${dto.dishId}",
                price = dto.unitPrice
            ),
            quantity = dto.quantity
        )
    } ?: emptyList()

    // Convertir SupplySelectionResponseDto a SupplySelection
    val supplies = supplySelections?.map { dto ->
        SupplySelection(
            option = SupplyOption(
                id = dto.supplyId,
                name = dto.supplyName ?: "Supply #${dto.supplyId}",
                description = "",
                unitPrice = dto.unitPrice
            ),
            quantity = dto.quantity
        )
    } ?: emptyList()

    return RegisteredSale(
        id = id,
        saleNumber = saleNumber ?: "SALE-${String.format("%04d", id)}",
        dishSelections = dishes,
        supplySelections = supplies,
        totalCost = totalCost,
        registeredDate = date
    )
}
