package com.uitopic.restockmobile.core.cloudinary.remote

import com.uitopic.restockmobile.core.cloudinary.models.CloudinaryUploadResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Url

interface CloudinaryApiService {

    @Multipart
    @POST
    suspend fun uploadImage(
        @Url url: String,
        @Part file: MultipartBody.Part,
        @Part("upload_preset") uploadPreset: RequestBody,
        @Part("folder") folder: RequestBody? = null
    ): Response<CloudinaryUploadResponse>
}