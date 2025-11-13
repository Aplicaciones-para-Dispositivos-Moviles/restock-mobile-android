package com.uitopic.restockmobile.features.monitoring.domain.usecases

import com.uitopic.restockmobile.core.utils.Resource
import com.uitopic.restockmobile.features.monitoring.data.repositories.SaleRepository
import com.uitopic.restockmobile.features.monitoring.domain.model.RegisteredSale
import javax.inject.Inject

class GetAllSalesUseCase @Inject constructor(
    private val repository: SaleRepository
) {
    suspend operator fun invoke(): Resource<List<RegisteredSale>> {
        return repository.getAllSales()
    }
}
