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

    @GET("profiles/{id}")
    suspend fun getProfileById(
        @Path("id") id: String
    ): Response<ProfileDto>

    @PUT("profiles/{id}/personal")
    suspend fun updatePersonalData(
        @Path("id") id: String,
        @Body request: UpdatePersonalDataDto
    ): Response<ProfileDto>

    @PUT("profiles/{id}/business")
    suspend fun updateBusinessData(
        @Path("id") id: String,
        @Body request: UpdateBusinessDataDto
    ): Response<ProfileDto>

    @PUT("profiles/{id}/password")
    suspend fun changePassword(
        @Path("id") id: String,
        @Body request: ChangePasswordDto
    ): Response<Unit>

    @DELETE("profiles/{id}")
    suspend fun deleteProfile(
        @Path("id") id: String
    ): Response<Unit>

    @GET("business-categories")
    suspend fun getAllBusinessCategories(): Response<List<CategoryDto>>
}