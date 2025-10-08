package com.uitopic.restockmobile.features.resources.domain.repositories

import com.uitopic.restockmobile.features.resources.domain.models.Batch
import com.uitopic.restockmobile.features.resources.domain.models.CustomSupply
import com.uitopic.restockmobile.features.resources.domain.models.Supply

interface InventoryRepository {
    suspend fun getSupplies(): List<Supply>
    suspend fun getCustomSupplies(): List<CustomSupply>
    suspend fun getBatches(): List<Batch>
    suspend fun createBatch(batch: Batch): Batch?
    suspend fun deleteBatch(batchId: String)
    suspend fun createCustomSupply(custom: CustomSupply): CustomSupply?
    suspend fun updateCustomSupply(custom: CustomSupply): CustomSupply?
    suspend fun deleteCustomSupply(customSupplyId: String)
}
