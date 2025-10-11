package com.uitopic.restockmobile.features.resources.data.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Named
import javax.inject.Singleton
import com.uitopic.restockmobile.features.resources.data.remote.services.InventoryService

@Module
@InstallIn(SingletonComponent::class)
object RemoteModule {

    @Provides
    @Singleton
    @Named("base_url")
    fun provideApiBaseUrl(): String {
        return "https://restock-platform-production.up.railway.app/api/v1/"
    }

    @Provides
    @Singleton
    fun provideRetrofit(@Named("base_url") url: String): Retrofit {
        return Retrofit
            .Builder()
            .baseUrl(url)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    @Provides
    @Singleton
    fun provideInventoryService(retrofit: Retrofit): InventoryService {
        return retrofit.create(InventoryService::class.java)
    }
}
