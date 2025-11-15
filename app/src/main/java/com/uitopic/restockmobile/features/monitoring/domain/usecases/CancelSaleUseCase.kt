package com.uitopic.restockmobile.features.monitoring.domain.usecases

import com.uitopic.restockmobile.core.utils.Resource
import com.uitopic.restockmobile.features.monitoring.data.repositories.SaleRepository
import javax.inject.Inject

class CancelSaleUseCase @Inject constructor(
    private val repository: SaleRepository
) {
    suspend operator fun invoke(saleId: Int): Resource<Unit> {
        return repository.cancelSale(saleId)
    }
}

