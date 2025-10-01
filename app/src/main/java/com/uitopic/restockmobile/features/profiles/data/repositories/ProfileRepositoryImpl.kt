package com.uitopic.restockmobile.features.profiles.data.repositories

import com.uitopic.restockmobile.features.profiles.domain.models.BusinessCategory
import com.uitopic.restockmobile.features.profiles.domain.models.ChangePasswordRequest
import com.uitopic.restockmobile.features.profiles.domain.models.Profile
import com.uitopic.restockmobile.features.profiles.domain.models.UpdateBusinessDataRequest
import com.uitopic.restockmobile.features.profiles.domain.models.UpdatePersonalDataRequest
import com.uitopic.restockmobile.features.profiles.domain.repositories.ProfileRepository
import javax.inject.Inject
import kotlinx.coroutines.delay

class ProfileRepositoryImpl @Inject constructor(
    // Cuando tengas el backend real, inyecta: private val apiService: ProfileApiService
) : ProfileRepository {

    // MOCK DATA - Datos de prueba
    private var mockProfile = Profile(
        id = "1",
        firstName = "Elon",
        lastName = "Musk",
        email = "elon@gmail.com",
        phone = "+51 940 163 899",
        address = "Av. Paseo de la Republica cuadra 2 - Perú",
        country = "Perú",
        avatar = null,
        businessName = "Sabrocito",
        businessAddress = "Av. Paseo de la Republica cuadra 2",
        description = null,
        categories = listOf(
            BusinessCategory("1", "Fast food"),
            BusinessCategory("2", "Bakery"),
            BusinessCategory("3", "Vegetarian"),
            BusinessCategory("4", "Buffet"),
            BusinessCategory("5", "Pizzeria"),
            BusinessCategory("6", "Grill")
        )
    )

    private val allCategories = listOf(
        BusinessCategory("1", "Fast food"),
        BusinessCategory("2", "Bakery"),
        BusinessCategory("3", "Vegetarian"),
        BusinessCategory("4", "Buffet"),
        BusinessCategory("5", "Pizzeria"),
        BusinessCategory("6", "Grill"),
        BusinessCategory("7", "Seafood"),
        BusinessCategory("8", "Asian"),
        BusinessCategory("9", "Mexican"),
        BusinessCategory("10", "Italian")
    )

    override suspend fun getProfileById(id: String): Result<Profile> {
        return try {
            delay(500) // Simula latencia de red
            Result.success(mockProfile)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun updatePersonalData(
        id: String,
        request: UpdatePersonalDataRequest
    ): Result<Profile> {
        return try {
            delay(800)
            mockProfile = mockProfile.copy(
                firstName = request.firstName,
                lastName = request.lastName,
                email = request.email,
                address = request.address,
                country = request.country
            )
            Result.success(mockProfile)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun updateBusinessData(
        id: String,
        request: UpdateBusinessDataRequest
    ): Result<Profile> {
        return try {
            delay(800)
            val selectedCategories = allCategories.filter {
                it.id in request.categoryIds
            }
            mockProfile = mockProfile.copy(
                businessName = request.businessName,
                businessAddress = request.businessAddress,
                categories = selectedCategories
            )
            Result.success(mockProfile)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun changePassword(
        id: String,
        request: ChangePasswordRequest
    ): Result<Unit> {
        return try {
            delay(800)
            // Simula validación de contraseña
            if (request.currentPassword.length < 6) {
                throw Exception("Current password is incorrect")
            }
            if (request.newPassword != request.confirmPassword) {
                throw Exception("Passwords do not match")
            }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun deleteProfile(id: String): Result<Unit> {
        return try {
            delay(1000)
            // Simula eliminación exitosa
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getAllBusinessCategories(): Result<List<BusinessCategory>> {
        return try {
            delay(300)
            Result.success(allCategories)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}

/*
 * IMPLEMENTACIÓN REAL CON RETROFIT (Descomenta cuando tengas el backend):
 *
class ProfileRepositoryImpl @Inject constructor(
    private val apiService: ProfileApiService
) : ProfileRepository {

    override suspend fun getProfileById(id: String): Result<Profile> {
        return try {
            val response = apiService.getProfileById(id)
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
            val response = apiService.updatePersonalData(id, request.toDto())
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
            val response = apiService.updateBusinessData(id, request.toDto())
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
            val response = apiService.changePassword(id, request.toDto())
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
            val response = apiService.deleteProfile(id)
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
}
*/