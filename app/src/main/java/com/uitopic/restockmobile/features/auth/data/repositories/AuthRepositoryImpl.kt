package com.uitopic.restockmobile.features.auth.data.repositories

import com.uitopic.restockmobile.core.auth.local.TokenManager
import com.uitopic.restockmobile.core.auth.remote.services.AuthApiService
import com.uitopic.restockmobile.features.auth.data.remote.mappers.toDomain
import com.uitopic.restockmobile.features.auth.data.remote.mappers.toDto
import com.uitopic.restockmobile.features.auth.domain.models.SignInRequest
import com.uitopic.restockmobile.features.auth.domain.models.SignUpRequest
import com.uitopic.restockmobile.features.auth.domain.models.User
import com.uitopic.restockmobile.features.auth.domain.repositories.AuthRepository
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(
    private val apiService: AuthApiService,
    private val tokenManager: TokenManager
) : AuthRepository {

    override suspend fun signUp(request: SignUpRequest): Result<User> {
        return try {
            val response = apiService.signUp(request.toDto())

            if (response.isSuccessful && response.body() != null) {
                val user = response.body()!!.toDomain()
                Result.success(user)
            } else {
                Result.failure(Exception("Sign up failed: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun signIn(request: SignInRequest): Result<User> {
        return try {
            val response = apiService.signIn(request.toDto())

            if (response.isSuccessful && response.body() != null) {
                val authResponse = response.body()!!
                val user = authResponse.toDomain()

                // Guardar token y datos del usuario
                authResponse.token?.let { tokenManager.saveToken(it) }
                tokenManager.saveUserData(user.id, user.username, user.roleId, user.subscription)

                Result.success(user)
            } else {
                Result.failure(Exception("Sign in failed: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun logout() {
        tokenManager.clearAll()
    }

    override fun isLoggedIn(): Boolean {
        return tokenManager.isLoggedIn()
    }

    override fun getCurrentUserId(): Int? {
        val userId = tokenManager.getUserId()
        return if (userId != -1) userId else null
    }


}