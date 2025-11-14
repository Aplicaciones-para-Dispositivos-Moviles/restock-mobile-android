package com.uitopic.restockmobile.features.resources.orders.data.di

import com.uitopic.restockmobile.features.resources.orders.data.repositories.OrdersRepositoryImpl
import com.uitopic.restockmobile.features.resources.orders.domain.repositories.OrdersRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class OrdersRepositoryModule {

    @Binds
    abstract fun bindOrdersRepository(
        impl: OrdersRepositoryImpl
    ): OrdersRepository
}