package com.uitopic.restockmobile.features.profiles.data.repositories

import com.uitopic.restockmobile.core.auth.local.TokenManager
import com.uitopic.restockmobile.features.profiles.data.remote.mappers.toDomain
import com.uitopic.restockmobile.features.profiles.data.remote.mappers.toDto
import com.uitopic.restockmobile.features.profiles.data.remote.services.ProfileApiService
import com.uitopic.restockmobile.features.profiles.domain.models.BusinessCategory
import com.uitopic.restockmobile.features.profiles.domain.models.ChangePasswordRequest
import com.uitopic.restockmobile.features.profiles.domain.models.Profile
import com.uitopic.restockmobile.features.profiles.domain.models.UpdateBusinessDataRequest
import com.uitopic.restockmobile.features.profiles.domain.models.UpdatePersonalDataRequest
import com.uitopic.restockmobile.features.profiles.domain.repositories.ProfileRepository
import javax.inject.Inject

class ProfileRepositoryImpl @Inject constructor(
    private val apiService: ProfileApiService,
    private val tokenManager: TokenManager
) : ProfileRepository {

    override suspend fun getProfileById(id: String): Result<Profile> {
        return try {
            val userId = id.toInt()  // Convertir String a Int
            val response = apiService.getProfileById(userId)

            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!.toDomain())
            } else {
                Result.failure(Exception("Error: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun updatePersonalData(
        id: String,
        request: UpdatePersonalDataRequest
    ): Result<Profile> {
        return try {
            val userId = id.toInt()
            val response = apiService.updatePersonalData(userId, request.toDto())

            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!.toDomain())
            } else {
                Result.failure(Exception("Error: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun updateBusinessData(
        id: String,
        request: UpdateBusinessDataRequest
    ): Result<Profile> {
        return try {
            val userId = id.toInt()
            val response = apiService.updateBusinessData(userId, request.toDto())

            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!.toDomain())
            } else {
                Result.failure(Exception("Error: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun changePassword(
        id: String,
        request: ChangePasswordRequest
    ): Result<Unit> {
        return try {
            val userId = id.toInt()
            val response = apiService.changePassword(userId, request.toDto())

            if (response.isSuccessful) {
                Result.success(Unit)
            } else {
                Result.failure(Exception("Error: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun deleteProfile(id: String): Result<Unit> {
        return try {
            val userId = id.toInt()
            val response = apiService.deleteProfile(userId)

            if (response.isSuccessful) {
                Result.success(Unit)
            } else {
                Result.failure(Exception("Error: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getAllBusinessCategories(): Result<List<BusinessCategory>> {
        return try {
            val response = apiService.getAllBusinessCategories()

            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!.map { it.toDomain() })
            } else {
                Result.failure(Exception("Error: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun logout() {
        tokenManager.clearAll()
    }
}