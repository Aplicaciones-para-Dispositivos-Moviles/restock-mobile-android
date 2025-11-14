package com.uitopic.restockmobile.features.monitoring.data.di

import com.uitopic.restockmobile.core.network.di.ApiRetrofit
import com.uitopic.restockmobile.features.monitoring.data.remote.services.SaleApiService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object MonitoringModule {

    @Provides
    @Singleton
    fun provideSaleApiService(@ApiRetrofit retrofit: Retrofit): SaleApiService {
        return retrofit.create(SaleApiService::class.java)
    }
}

