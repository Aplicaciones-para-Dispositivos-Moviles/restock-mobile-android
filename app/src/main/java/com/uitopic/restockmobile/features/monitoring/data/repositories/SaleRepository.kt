package com.uitopic.restockmobile.features.monitoring.data.repositories

import com.uitopic.restockmobile.core.utils.Resource
import com.uitopic.restockmobile.features.monitoring.data.remote.mappers.toDomain
import com.uitopic.restockmobile.features.monitoring.data.remote.mappers.toDto
import com.uitopic.restockmobile.features.monitoring.data.remote.models.CreateSaleDto
import com.uitopic.restockmobile.features.monitoring.data.remote.services.SaleApiService
import com.uitopic.restockmobile.features.monitoring.domain.model.DishSelection
import com.uitopic.restockmobile.features.monitoring.domain.model.RegisteredSale
import com.uitopic.restockmobile.features.monitoring.domain.model.SupplySelection
import javax.inject.Inject

class SaleRepository @Inject constructor(
    private val saleApiService: SaleApiService
) {
    suspend fun createSale(
        dishSelections: List<DishSelection>,
        supplySelections: List<SupplySelection>,
        userId: Int
    ): Resource<RegisteredSale> {
        return try {
            val dishTotal = dishSelections.sumOf { it.option.price * it.quantity }
            val supplyTotal = supplySelections.sumOf { it.option.unitPrice * it.quantity }
            val subtotal = dishTotal + supplyTotal
            val taxes = subtotal * 0.08
            val totalCost = subtotal + taxes

            val createSaleDto = CreateSaleDto(
                dishSelections = dishSelections.map { it.toDto() },
                supplySelections = supplySelections.map { it.toDto() },
                subtotal = subtotal,
                taxes = taxes,
                totalCost = totalCost,
                userId = userId
            )

            val response = saleApiService.createSale(createSaleDto)

            if (response.isSuccessful && response.body() != null) {
                val saleDto = response.body()!!
                val registeredSale = saleDto.toDomain()
                Resource.Success(registeredSale)
            } else {
                Resource.Error("Error creating sale: ${response.message()}")
            }
        } catch (e: Exception) {
            Resource.Error("Network error: ${e.message}")
        }
    }

    suspend fun getAllSales(): Resource<List<RegisteredSale>> {
        return try {
            val response = saleApiService.getAllSales()

            if (response.isSuccessful && response.body() != null) {
                val sales = response.body()!!.map { saleDto ->
                    saleDto.toDomain()
                }
                Resource.Success(sales)
            } else {
                Resource.Error("Error fetching sales: ${response.message()}")
            }
        } catch (e: Exception) {
            Resource.Error("Network error: ${e.message}")
        }
    }

    suspend fun cancelSale(saleId: Int): Resource<Unit> {
        return try {
            val response = saleApiService.cancelSale(saleId)

            if (response.isSuccessful) {
                Resource.Success(Unit)
            } else {
                Resource.Error("Error cancelling sale: ${response.message()}")
            }
        } catch (e: Exception) {
            Resource.Error("Network error: ${e.message}")
        }
    }
}
