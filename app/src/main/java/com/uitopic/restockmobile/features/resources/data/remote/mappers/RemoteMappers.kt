package com.uitopic.restockmobile.features.resources.data.remote.mappers

import CustomSupplyDto
import CustomSupplyRequestDto
import com.uitopic.restockmobile.features.resources.data.remote.models.BatchDto
import com.uitopic.restockmobile.features.resources.data.remote.models.SupplyDto
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
        id = id ?: 0,
        description = description.orEmpty(),
        minStock = minStock ?: 0,
        maxStock = maxStock ?: 0,
        price = price ?: 0.0,
        userId = userId,
        supplyId = supply?.id ?: 0,
        supply = supply?.toDomain(),
        unit = UnitModel(unitName.orEmpty(), unitAbbreviaton.orEmpty()),
        currencyCode = currencyCode.orEmpty()
    )

fun CustomSupply.toRequestDto(userId: Int): CustomSupplyRequestDto =
    CustomSupplyRequestDto(
        id = if (id == 0) null else id,
        supplyId = supplyId,
        description = description,
        minStock = minStock,
        maxStock = maxStock,
        price = price,
        userId = this.userId ?: userId,
        unitName = unit.name,
        unitAbbreviaton = unit.abbreviation
    )