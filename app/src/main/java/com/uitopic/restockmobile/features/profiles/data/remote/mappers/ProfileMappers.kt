package com.uitopic.restockmobile.features.profiles.data.remote.mappers

import com.uitopic.restockmobile.features.profiles.data.remote.models.CategoryDto
import com.uitopic.restockmobile.features.profiles.data.remote.models.ChangePasswordDto
import com.uitopic.restockmobile.features.profiles.data.remote.models.ProfileDto
import com.uitopic.restockmobile.features.profiles.data.remote.models.UpdateBusinessDataDto
import com.uitopic.restockmobile.features.profiles.data.remote.models.UpdatePersonalDataDto
import com.uitopic.restockmobile.features.profiles.domain.models.BusinessCategory
import com.uitopic.restockmobile.features.profiles.domain.models.ChangePasswordRequest
import com.uitopic.restockmobile.features.profiles.domain.models.Profile
import com.uitopic.restockmobile.features.profiles.domain.models.UpdateBusinessDataRequest
import com.uitopic.restockmobile.features.profiles.domain.models.UpdatePersonalDataRequest



fun ProfileDto.toDomain(): Profile {
    return Profile(
        id = userId,
        firstName = firstName ?: "",
        lastName = lastName ?: "",
        email = email ?: "",
        phone = phone ?: "",
        address = address ?: "",
        country = country ?: "",
        avatar = avatar,
        businessName = businessName ?: "",
        businessAddress = businessAddress ?: "",
        description = description ?: "",
        categories = businessCategories?.map { it.toDomain() } ?: emptyList()
    )
}

fun CategoryDto.toDomain(): BusinessCategory {
    return BusinessCategory(
        id = id,
        name = name
    )
}

fun UpdatePersonalDataRequest.toDto(): UpdatePersonalDataDto {
    return UpdatePersonalDataDto(
        firstName = firstName,
        lastName = lastName,
        email = email,
        phone = phone,
        address = address,
        country = country,
        avatar = avatar
    )
}

fun UpdateBusinessDataRequest.toDto(): UpdateBusinessDataDto {
    return UpdateBusinessDataDto(
        businessName = businessName,
        businessAddress = businessAddress,
        description = description,
        businessCategoryIds = categoryIds
    )
}

fun ChangePasswordRequest.toDto(): ChangePasswordDto {
    return ChangePasswordDto(
        currentPassword = currentPassword,
        newPassword = newPassword
    )
}