package com.uitopic.restockmobile.core.auth.remote.services

import com.uitopic.restockmobile.core.auth.remote.models.AuthResponseDto
import com.uitopic.restockmobile.core.auth.remote.models.SignInRequestDto
import com.uitopic.restockmobile.core.auth.remote.models.SignUpRequestDto
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface AuthApiService {

    @POST("authentication/sign-up")
    suspend fun signUp(
        @Body request: SignUpRequestDto
    ): Response<AuthResponseDto>

    @POST("authentication/sign-in")
    suspend fun signIn(
        @Body request: SignInRequestDto
    ): Response<AuthResponseDto>
}
