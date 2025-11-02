/*package com.uitopic.restockmobile.features.resources.data.di

import com.uitopic.restockmobile.features.resources.data.remote.services.InventoryService
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    @Singleton
    fun provideMoshi(): Moshi =
        Moshi.Builder()
            .add(KotlinJsonAdapterFactory())
            .build()

    @Provides
    @Singleton
    fun provideRetrofit(moshi: Moshi): Retrofit =
        Retrofit.Builder()
            .baseUrl("https://tu-api-base-url/") //
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()

    @Provides
    @Singleton
    fun provideInventoryService(retrofit: Retrofit): InventoryService =
        retrofit.create(InventoryService::class.java)
}
*/

package com.uitopic.restockmobile.features.resources.data.di

import com.uitopic.restockmobile.features.resources.data.remote.services.FakeInventoryService
import com.uitopic.restockmobile.features.resources.data.remote.services.InventoryService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    @Singleton
    fun provideInventoryService(): InventoryService = FakeInventoryService()
}
