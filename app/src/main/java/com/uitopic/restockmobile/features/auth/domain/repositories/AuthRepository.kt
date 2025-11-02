package com.uitopic.restockmobile.features.auth.domain.repositories

import com.uitopic.restockmobile.features.auth.domain.models.SignInRequest
import com.uitopic.restockmobile.features.auth.domain.models.SignUpRequest
import com.uitopic.restockmobile.features.auth.domain.models.User

interface AuthRepository {
    suspend fun signUp(request: SignUpRequest): Result<User>
    suspend fun signIn(request: SignInRequest): Result<User>
    suspend fun logout()
    fun isLoggedIn(): Boolean
    fun getCurrentUserId(): Int?
}
