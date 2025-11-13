package com.uitopic.restockmobile.features.monitoring.domain.usecases

import com.uitopic.restockmobile.core.utils.Resource
import com.uitopic.restockmobile.features.monitoring.data.repositories.SaleRepository
import com.uitopic.restockmobile.features.monitoring.domain.model.DishSelection
import com.uitopic.restockmobile.features.monitoring.domain.model.RegisteredSale
import com.uitopic.restockmobile.features.monitoring.domain.model.SupplySelection
import javax.inject.Inject

class CreateSaleUseCase @Inject constructor(
    private val repository: SaleRepository
) {
    suspend operator fun invoke(
        dishSelections: List<DishSelection>,
        supplySelections: List<SupplySelection>,
        userId: Int
    ): Resource<RegisteredSale> {
        return repository.createSale(dishSelections, supplySelections, userId)
    }
}

