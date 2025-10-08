package com.uitopic.restockmobile.features.resources.data.repositories

import com.uitopic.restockmobile.features.resources.data.local.dao.BatchDao
import com.uitopic.restockmobile.features.resources.data.local.mappers.toDomain
import com.uitopic.restockmobile.features.resources.data.local.models.BatchEntity
import com.uitopic.restockmobile.features.resources.data.remote.mappers.toDomain
import com.uitopic.restockmobile.features.resources.data.remote.mappers.toDto
import com.uitopic.restockmobile.features.resources.data.remote.models.BatchDto
import com.uitopic.restockmobile.features.resources.data.remote.services.InventoryService
import com.uitopic.restockmobile.features.resources.domain.models.Batch
import com.uitopic.restockmobile.features.resources.domain.models.CustomSupply
import com.uitopic.restockmobile.features.resources.domain.models.Supply
import com.uitopic.restockmobile.features.resources.domain.repositories.InventoryRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class InventoryRepositoryImpl @Inject constructor(
    private val service: InventoryService,
    private val dao: BatchDao?
) : InventoryRepository {

    override suspend fun getSupplies(): List<Supply> = withContext(Dispatchers.IO) {
        val resp = service.getSupplies()
        if (resp.isSuccessful) {
            resp.body()?.map { dto -> dto.toDomain() } ?: emptyList()
        } else emptyList()
    }

    override suspend fun getCustomSupplies(): List<CustomSupply> = withContext(Dispatchers.IO) {
        val resp = service.getCustomSupplies()
        if (resp.isSuccessful) {
            resp.body()?.map { dto -> dto.toDomain() } ?: emptyList()
        } else emptyList()
    }

    override suspend fun getBatches(): List<Batch> = withContext(Dispatchers.IO) {
        val resp = service.getBatches()
        val remote: List<Batch> = if (resp.isSuccessful) {
            resp.body()?.map { dto -> dto.toDomain() } ?: emptyList()
        } else emptyList()

        if (dao != null) {
            val local = dao.fetchAll().map { e -> e.toDomain() }
            val map = (local + remote).associateBy { it.id }
            map.values.toList()
        } else remote
    }

    override suspend fun createBatch(batch: Batch): Batch? = withContext(Dispatchers.IO) {
        val dto = BatchDto(
            _id = null,
            user_id = batch.userId,
            custom_supply = batch.customSupply?.toDto(),
            stock = batch.stock,
            expiration_date = batch.expirationDate
        )

        val resp = service.createBatch(dto)
        val new = if (resp.isSuccessful) {
            resp.body()?.toDomain()
        } else null

        if (new != null && dao != null) {
            val entity = BatchEntity(
                id = new.id,
                userId = new.userId,
                customSupplyId = new.customSupply?.id ?: "",
                stock = new.stock,
                expirationDate = new.expirationDate
            )
            dao.insert(entity)
        }
        new
    }

    override suspend fun deleteBatch(batchId: String): Unit = withContext(Dispatchers.IO) {
        service.deleteBatch(batchId)
        // remove local if present
        dao?.fetchAll()?.find { it.id == batchId }?.let {
            dao.delete(it)
        }
    }

    override suspend fun createCustomSupply(custom: CustomSupply): CustomSupply? = withContext(Dispatchers.IO) {
        val dto = custom.toDto()
        val resp = (service as? com.uitopic.restockmobile.features.resources.data.remote.services.FakeInventoryService)
            ?.createCustomSupply(dto)
        if (resp != null && resp.isSuccessful) resp.body()?.toDomain() else null
    }

    override suspend fun updateCustomSupply(custom: CustomSupply): CustomSupply? = withContext(Dispatchers.IO) {
        val dto = custom.toDto()
        val resp = (service as? com.uitopic.restockmobile.features.resources.data.remote.services.FakeInventoryService)
            ?.updateCustomSupply(dto)
        if (resp != null && resp.isSuccessful) resp.body()?.toDomain() else null
    }
    override suspend fun deleteCustomSupply(customSupplyId: String): Unit = withContext(Dispatchers.IO) {
        try {
            service.deleteCustomSupply(customSupplyId)
        } catch (t: Throwable) {
            // ignore / log
        }
    }

    override suspend fun updateBatch(batch: Batch): Batch? = withContext(Dispatchers.IO) {
        val dto = BatchDto(
            _id = batch.id,
            user_id = batch.userId,
            custom_supply = batch.customSupply?.toDto(),
            stock = batch.stock,
            expiration_date = batch.expirationDate
        )

        val resp = service.updateBatch(batch.id, dto)
        val updated = if (resp.isSuccessful) {
            resp.body()?.toDomain()
        } else null

        if (updated != null && dao != null) {
            val entity = BatchEntity(
                id = updated.id,
                userId = updated.userId,
                customSupplyId = updated.customSupply?.id ?: "",
                stock = updated.stock,
                expirationDate = updated.expirationDate
            )
            dao.insert(entity) // upsert localmente
        }

        updated
    }
}