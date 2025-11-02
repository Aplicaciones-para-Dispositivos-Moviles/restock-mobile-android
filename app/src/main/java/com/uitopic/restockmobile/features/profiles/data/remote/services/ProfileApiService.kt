package com.uitopic.restockmobile.features.profiles.data.remote.services

import com.uitopic.restockmobile.features.profiles.data.remote.models.CategoryDto
import com.uitopic.restockmobile.features.profiles.data.remote.models.ChangePasswordDto
import com.uitopic.restockmobile.features.profiles.data.remote.models.ProfileDto
import com.uitopic.restockmobile.features.profiles.data.remote.models.UpdateBusinessDataDto
import com.uitopic.restockmobile.features.profiles.data.remote.models.UpdatePersonalDataDto
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.PUT
import retrofit2.http.Path

interface ProfileApiService {

    @GET("profiles/{userId}")
    suspend fun getProfileById(
        @Path("userId") userId: Int
    ): Response<ProfileDto>

    @PUT("profiles/{userId}/personal")
    suspend fun updatePersonalData(
        @Path("userId") userId: Int,
        @Body request: UpdatePersonalDataDto
    ): Response<ProfileDto>

    @PUT("profiles/{userId}/business")
    suspend fun updateBusinessData(
        @Path("userId") userId: Int,
        @Body request: UpdateBusinessDataDto
    ): Response<ProfileDto>

    @PUT("profiles/{userId}/password")
    suspend fun changePassword(
        @Path("userId") userId: Int,
        @Body request: ChangePasswordDto
    ): Response<Unit>

    @DELETE("profiles/{userId}")
    suspend fun deleteProfile(
        @Path("userId") userId: Int
    ): Response<Unit>

    @GET("business-categories")
    suspend fun getAllBusinessCategories(): Response<List<CategoryDto>>
}