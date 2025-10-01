package com.uitopic.restockmobile.core.cloudinary.di

import com.uitopic.restockmobile.core.cloudinary.remote.CloudinaryApiService
import com.uitopic.restockmobile.core.cloudinary.repositories.CloudinaryImageUploadRepositoryImpl
import com.uitopic.restockmobile.core.cloudinary.repositories.ImageUploadRepository
import com.uitopic.restockmobile.core.network.di.CloudinaryRetrofit
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Named
import javax.inject.Singleton

// core/cloudinary/di/CloudinaryModule.kt
@Module
@InstallIn(SingletonComponent::class)
object CloudinaryModule {

    @Provides
    @Singleton
    @Named("CloudinaryOkHttp")  // Nombre diferente
    fun provideCloudinaryOkHttpClient(): OkHttpClient {
        return OkHttpClient.Builder()
            .connectTimeout(60, TimeUnit.SECONDS)
            .readTimeout(60, TimeUnit.SECONDS)
            .writeTimeout(60, TimeUnit.SECONDS)
            .build()
    }

    @Provides
    @Singleton
    @CloudinaryRetrofit  // ← Qualifier personalizado
    fun provideCloudinaryRetrofit(
        @Named("CloudinaryOkHttp") okHttpClient: OkHttpClient
    ): Retrofit {
        return Retrofit.Builder()
            .baseUrl("https://api.cloudinary.com/")
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    @Provides
    @Singleton
    fun provideCloudinaryApiService(
        @CloudinaryRetrofit retrofit: Retrofit
    ): CloudinaryApiService {
        return retrofit.create(CloudinaryApiService::class.java)
    }
}

@Module
@InstallIn(SingletonComponent::class)
abstract class CloudinaryRepositoryModule {

    @Binds
    @Singleton
    abstract fun bindImageUploadRepository(
        impl: CloudinaryImageUploadRepositoryImpl
    ): ImageUploadRepository
}