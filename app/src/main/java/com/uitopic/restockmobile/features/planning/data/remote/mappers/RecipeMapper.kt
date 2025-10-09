package com.uitopic.restockmobile.features.planning.data.remote.mappers

import com.uitopic.restockmobile.features.planning.data.remote.models.AddSupplyToRecipeDto
import com.uitopic.restockmobile.features.planning.data.remote.models.CreateRecipeDto
import com.uitopic.restockmobile.features.planning.data.remote.models.RecipeDto
import com.uitopic.restockmobile.features.planning.data.remote.models.RecipeSupplyDto
import com.uitopic.restockmobile.features.planning.data.remote.models.UpdateRecipeDto
import com.uitopic.restockmobile.features.planning.data.remote.models.UpdateRecipeSupplyDto
import com.uitopic.restockmobile.features.planning.domain.models.AddSupplyToRecipeRequest
import com.uitopic.restockmobile.features.planning.domain.models.CreateRecipeRequest
import com.uitopic.restockmobile.features.planning.domain.models.Recipe
import com.uitopic.restockmobile.features.planning.domain.models.RecipeSupply
import com.uitopic.restockmobile.features.planning.domain.models.UpdateRecipeRequest
import com.uitopic.restockmobile.features.planning.domain.models.UpdateRecipeSupplyRequest

fun RecipeDto.toDomain(): Recipe {
    return Recipe(
        id = id ?: 0,
        name = name ?: "",
        description = description ?: "",
        imageUrl = imageUrl ?: "",
        price = price ?: 0.0,
        userId = userId ?: 0,
        supplies = supplies?.map { it.toDomain() } ?: emptyList()
    )
}

fun RecipeSupplyDto.toDomain(): RecipeSupply {
    return RecipeSupply(
        supplyId = (supplyId as? Int) ?: 0,
        quantity = quantity ?: 0.0
    )
}

fun CreateRecipeRequest.toDto(): CreateRecipeDto {
    return CreateRecipeDto(
        name = name,
        description = description,
        imageUrl = imageUrl.toString(),
        price = price,
        userId = userId
    )
}

fun UpdateRecipeRequest.toDto(): UpdateRecipeDto {
    return UpdateRecipeDto(
        name = name,
        description = description,
        imageUrl = imageUrl.toString(),
        price = price
    )
}


fun AddSupplyToRecipeRequest.toDto(): AddSupplyToRecipeDto {
    return AddSupplyToRecipeDto(
        supplyId = supplyId.toString(),
        quantity = quantity
    )
}

fun UpdateRecipeSupplyRequest.toDto(): UpdateRecipeSupplyDto {
    return UpdateRecipeSupplyDto(
        supplyId = supplyId.toString(),
        quantity = quantity
    )
}