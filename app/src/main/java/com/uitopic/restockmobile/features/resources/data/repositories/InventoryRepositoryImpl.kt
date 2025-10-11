package com.uitopic.restockmobile.features.resources.data.repositories

import android.util.Log
import com.uitopic.restockmobile.core.auth.local.TokenManager
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
    private val tokenManager: TokenManager
) : InventoryRepository {

    // ---------------------------------------------------------
    // SUPPLIES
    // ---------------------------------------------------------
    override suspend fun getSupplies(): List<Supply> = withContext(Dispatchers.IO) {
        val resp = service.getSupplies()
        if (resp.isSuccessful) {
            resp.body()?.map { it.toDomain() } ?: emptyList()
        } else emptyList()
    }

    // ---------------------------------------------------------
    // CUSTOM SUPPLIES
    // ---------------------------------------------------------
    override suspend fun getCustomSupplies(): List<CustomSupply> = withContext(Dispatchers.IO) {
        val resp = service.getCustomSupplies()
        if (resp.isSuccessful) {
            resp.body()?.map { it.toDomain() } ?: emptyList()
        } else emptyList()
    }

    override suspend fun createCustomSupply(custom: CustomSupply): CustomSupply? =
        withContext(Dispatchers.IO) {
            try {
                val userId = tokenManager.getUserId()
                Log.d("InventoryRepository", "User ID obtenido: $userId")

                if (userId == null) {
                    Log.e("InventoryRepository", "User ID es null, no se puede crear supply.")
                    return@withContext null
                }

                val dto = custom.toDto(userId)
                Log.d("InventoryRepository", "DTO enviado: $dto")

                val response = service.createCustomSupply(dto)
                Log.d("InventoryRepository", "Response code: ${response.code()} - success: ${response.isSuccessful}")

                if (response.isSuccessful) {
                    val body = response.body()
                    Log.d("InventoryRepository", "Respuesta exitosa: $body")
                    body?.toDomain()
                } else {
                    Log.e("InventoryRepository", "Error del servidor: ${response.errorBody()?.string()}")
                    null
                }
            } catch (e: Exception) {
                Log.e("InventoryRepository", "Excepción al crear supply: ${e.message}", e)
                null
            }
        }

    override suspend fun updateCustomSupply(custom: CustomSupply): CustomSupply? =
        withContext(Dispatchers.IO) {
            try {
                val userId = tokenManager.getUserId() ?: return@withContext null
                val id = custom.id ?: return@withContext null
                val dto = custom.toDto(userId)
                val response = service.updateCustomSupply(id, dto)
                if (response.isSuccessful) response.body()?.toDomain() else null
            } catch (e: Exception) {
                null
            }
        }

    override suspend fun deleteCustomSupply(customSupplyId: Int): Unit =
        withContext(Dispatchers.IO) {
            try {
                service.deleteCustomSupply(customSupplyId.toInt())
            } catch (_: Exception) {
                // Ignorar errores
            }
        }

    // ---------------------------------------------------------
    // BATCHES
    // ---------------------------------------------------------
    override suspend fun getBatches(): List<Batch> = withContext(Dispatchers.IO) {
        val resp = service.getBatches()
        if (resp.isSuccessful) {
            resp.body()?.map { it.toDomain() } ?: emptyList()
        } else emptyList()
    }

    override suspend fun createBatch(batch: Batch): Batch? = withContext(Dispatchers.IO) {
        try {
            // Creamos el DTO
            val dto = BatchDto(
                id = null,
                userId = batch.userId,
                customSupplyId = batch.customSupply?.id,
                stock = batch.stock,
                expirationDate = batch.expirationDate
            )

            // Log del DTO enviado
            Log.d("InventoryRepository", "Enviando createBatch DTO: $dto")

            // Llamada al service
            val resp = service.createBatch(dto)

            // Log de la respuesta
            Log.d("InventoryRepository", "Response code: ${resp.code()}, isSuccessful: ${resp.isSuccessful}")
            Log.d("InventoryRepository", "Response body: ${resp.body()}")

            // Retorno del body mapeado a domain
            if (resp.isSuccessful) {
                val domainBatch = resp.body()?.toDomain()
                Log.d("InventoryRepository", "Batch creado en domain: $domainBatch")
                domainBatch
            } else {
                Log.e("InventoryRepository", "Error al crear batch: ${resp.errorBody()?.string()}")
                null
            }
        } catch (e: Exception) {
            Log.e("InventoryRepository", "Excepción en createBatch: ${e.message}", e)
            null
        }
    }

    override suspend fun updateBatch(batch: Batch): Batch? = withContext(Dispatchers.IO) {
        try {
            val dto = BatchDto(
                id = batch.id,
                userId = batch.userId,
                customSupplyId = batch.customSupply?.id,
                stock = batch.stock,
                expirationDate = batch.expirationDate
            )

            val resp = service.updateBatch(batch.id, dto)
            if (resp.isSuccessful) resp.body()?.toDomain() else null
        } catch (e: Exception) {
            null
        }
    }


    override suspend fun deleteBatch(batchId: String): Unit = withContext(Dispatchers.IO) {
        try {
            service.deleteBatch(batchId)
        } catch (_: Exception) {
            // Ignorar errores
        }
    }
}
