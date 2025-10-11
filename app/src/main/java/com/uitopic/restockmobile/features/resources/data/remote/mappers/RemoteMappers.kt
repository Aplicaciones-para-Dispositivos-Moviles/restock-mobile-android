package com.uitopic.restockmobile.features.resources.data.remote.mappers

import com.uitopic.restockmobile.features.resources.data.remote.models.BatchDto
import com.uitopic.restockmobile.features.resources.data.remote.models.CustomSupplyDto
import com.uitopic.restockmobile.features.resources.data.remote.models.SupplyDto
import com.uitopic.restockmobile.features.resources.data.remote.models.UnitDto
import com.uitopic.restockmobile.features.resources.domain.models.Batch
import com.uitopic.restockmobile.features.resources.domain.models.CustomSupply
import com.uitopic.restockmobile.features.resources.domain.models.Supply
import com.uitopic.restockmobile.features.resources.domain.models.UnitModel

fun SupplyDto.toDomain(): Supply =
    Supply(
        id = this.id ?: 0,
        name = this.name ?: "",
        description = this.description,
        perishable = this.perishable ?: false,
        category = this.category
    )

fun BatchDto.toDomain(customSupplies: List<CustomSupply>? = null): Batch =
    Batch(
        id = this.id ?: "",
        userId = this.userId,
        customSupply = this.customSupplyId?.let { id ->
            customSupplies?.find { it.id == id } ?: CustomSupply(
                id = id,
                minStock = 0,
                maxStock = 0,
                price = 0.0,
                userId = null,
                supplyId = 0,
                supply = null,
                unit = UnitModel("", ""),
                currencyCode = "",
                description = ""
            )
        },
        stock = this.stock ?: 0,
        expirationDate = this.expirationDate
    )
fun Batch.toDto(): BatchDto =
    BatchDto(
        id = this.id,
        userId = this.userId,
        customSupplyId = this.customSupply?.id, // enviar solo id como Int
        stock = this.stock,
        expirationDate = this.expirationDate
    )
// ---------------------------------------------------------
// CUSTOM SUPPLY
// ---------------------------------------------------------
fun CustomSupplyDto.toDomain(): CustomSupply =
    CustomSupply(
        id = this.id!!.toInt(),
        description = this.description,
        minStock = this.minStock,
        maxStock = this.maxStock,
        price = this.price,
        currencyCode = this.currencyCode,
        userId = this.userId,
        supplyId = this.supplyId,
        supply = Supply(
            id = this.supplyId,
            name = "",
            description = this.description,
            perishable = false,
            category = null
        ),
        unit = UnitModel(name = "", abbreviation = "")
    )

fun CustomSupply.toDto(userId: Int): CustomSupplyDto =
    CustomSupplyDto(
        id = this.id,
        supplyId = this.supplyId,
        description = this.description,
        minStock = this.minStock,
        maxStock = this.maxStock,
        price = this.price,
        currencyCode = this.currencyCode,
        userId = userId
    )