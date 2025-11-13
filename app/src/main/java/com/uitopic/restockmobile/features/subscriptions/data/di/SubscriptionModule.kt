package com.uitopic.restockmobile.features.subscriptions.data.di

import com.uitopic.restockmobile.core.network.di.ApiRetrofit
import com.uitopic.restockmobile.features.subscriptions.data.remote.services.SubscriptionApiService
import com.uitopic.restockmobile.features.subscriptions.data.repositories.SubscriptionRepositoryImpl
import com.uitopic.restockmobile.features.subscriptions.domain.repositories.SubscriptionRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object SubscriptionModule {

    @Provides
    @Singleton
    fun provideSubscriptionApiService(@ApiRetrofit retrofit: Retrofit): SubscriptionApiService {
        return retrofit.create(SubscriptionApiService::class.java)
    }

    @Provides
    @Singleton
    fun provideSubscriptionRepository(
        repositoryImpl: SubscriptionRepositoryImpl
    ): SubscriptionRepository {
        return repositoryImpl
    }
}
