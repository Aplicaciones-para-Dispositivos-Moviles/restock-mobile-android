package com.uitopic.restockmobile.core.network.di

import com.uitopic.restockmobile.core.auth.local.TokenManager
import com.uitopic.restockmobile.core.auth.remote.services.AuthApiService
import com.uitopic.restockmobile.core.network.ApiConstants
import com.uitopic.restockmobile.core.network.AuthInterceptor
import com.uitopic.restockmobile.features.planning.data.remote.services.RecipeApiService
import com.uitopic.restockmobile.features.profiles.data.remote.services.ProfileApiService
import com.uitopic.restockmobile.features.resources.data.remote.services.InventoryService
import com.uitopic.restockmobile.features.resources.orders.data.remote.services.OrdersService

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Named
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    @Singleton
    fun provideLoggingInterceptor(): HttpLoggingInterceptor {
        return HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }
    }

    @Provides
    @Singleton
    fun provideAuthInterceptor(tokenManager: TokenManager): AuthInterceptor {
        return AuthInterceptor(tokenManager)
    }

    @Provides
    @Singleton
    @Named("ApiOkHttp")
    fun provideOkHttpClient(
        authInterceptor: AuthInterceptor,
        loggingInterceptor: HttpLoggingInterceptor
    ): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(authInterceptor)
            .addInterceptor(loggingInterceptor)
            .connectTimeout(ApiConstants.CONNECT_TIMEOUT, TimeUnit.SECONDS)
            .readTimeout(ApiConstants.READ_TIMEOUT, TimeUnit.SECONDS)
            .writeTimeout(ApiConstants.WRITE_TIMEOUT, TimeUnit.SECONDS)
            .build()
    }

    @Provides
    @Singleton
    @ApiRetrofit
    fun provideRetrofit(@Named("ApiOkHttp") okHttpClient: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .baseUrl(ApiConstants.BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    @Provides
    @Singleton
    fun provideAuthApiService(@ApiRetrofit retrofit: Retrofit): AuthApiService {
        return retrofit.create(AuthApiService::class.java)
    }

    @Provides
    @Singleton
    fun provideProfileApiService(@ApiRetrofit retrofit: Retrofit): ProfileApiService {
        return retrofit.create(ProfileApiService::class.java)
    }

    @Provides
    @Singleton
    fun provideRecipeApiService(@ApiRetrofit retrofit: Retrofit): RecipeApiService {
        return retrofit.create(RecipeApiService::class.java)
    }

    @Provides
    @Singleton
    fun provideInventoryService(@ApiRetrofit retrofit: Retrofit): InventoryService {
        return retrofit.create(InventoryService::class.java)
    }

    @Provides
    @Singleton
    fun provideOrdersApiService(@ApiRetrofit retrofit: Retrofit): OrdersService =
        retrofit.create(OrdersService::class.java)
}