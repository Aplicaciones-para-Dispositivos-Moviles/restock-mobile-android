package com.uitopic.restockmobile.features.profiles.domain.repositories

import com.uitopic.restockmobile.features.profiles.domain.models.BusinessCategory
import com.uitopic.restockmobile.features.profiles.domain.models.ChangePasswordRequest
import com.uitopic.restockmobile.features.profiles.domain.models.Profile
import com.uitopic.restockmobile.features.profiles.domain.models.UpdateBusinessDataRequest
import com.uitopic.restockmobile.features.profiles.domain.models.UpdatePersonalDataRequest

interface ProfileRepository {
    suspend fun getProfileById(id: String): Result<Profile>

    suspend fun updatePersonalData(
        id: String,
        request: UpdatePersonalDataRequest
    ): Result<Profile>

    suspend fun updateBusinessData(
        id: String,
        request: UpdateBusinessDataRequest
    ): Result<Profile>

    suspend fun changePassword(
        id: String,
        request: ChangePasswordRequest
    ): Result<Unit>

    suspend fun deleteProfile(id: String): Result<Unit>

    suspend fun getAllBusinessCategories(): Result<List<BusinessCategory>>

    suspend fun logout()
}