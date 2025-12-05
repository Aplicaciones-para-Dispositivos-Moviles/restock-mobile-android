package com.uitopic.restockmobile.features.monitoring.domain.usecases

import com.uitopic.restockmobile.core.utils.Resource
import com.uitopic.restockmobile.features.monitoring.data.repositories.SaleRepository
import com.uitopic.restockmobile.features.monitoring.domain.model.DishSelection
import com.uitopic.restockmobile.features.monitoring.domain.model.RegisteredSale
import com.uitopic.restockmobile.features.monitoring.domain.model.SupplySelection
import com.uitopic.restockmobile.features.resources.inventory.domain.repositories.InventoryRepository
import javax.inject.Inject

class CreateSaleUseCase @Inject constructor(
    private val repository: SaleRepository,
    private val inventoryRepository: InventoryRepository
) {
    suspend operator fun invoke(
        dishSelections: List<DishSelection>,
        supplySelections: List<SupplySelection>,
        userId: Int
    ): Resource<RegisteredSale> {
        // Crear la venta primero
        val result = repository.createSale(dishSelections, supplySelections, userId)

        // Si la venta fue exitosa, actualizar el stock de los supplies
        if (result is Resource.Success && supplySelections.isNotEmpty()) {
            updateSupplyStocks(supplySelections)
        }

        return result
    }

    private suspend fun updateSupplyStocks(supplySelections: List<SupplySelection>) {
        try {
            // Obtener todos los custom supplies del usuario
            val customSupplies = inventoryRepository.getCustomSuppliesByUserId()

            // Para cada supply seleccionado en la venta
            supplySelections.forEach { selection ->
                // Encontrar el custom supply correspondiente
                val customSupply = customSupplies.find { it.id == selection.option.id }

                if (customSupply != null) {
                    // Calcular el nuevo stock máximo (restar la cantidad vendida)
                    val newMaxStock = customSupply.maxStock - selection.quantity

                    // Actualizar el custom supply con el nuevo stock
                    val updatedSupply = customSupply.copy(maxStock = newMaxStock)
                    inventoryRepository.updateCustomSupply(updatedSupply)
                }
            }
        } catch (e: Exception) {
            // Log error but don't fail the sale
            // El error en la actualización del stock no debe impedir que la venta se complete
        }
    }
}
