package com.uitopic.restockmobile.features.resources.inventory.data.di

import com.uitopic.restockmobile.features.resources.inventory.data.repositories.InventoryRepositoryImpl
import com.uitopic.restockmobile.features.resources.inventory.domain.repositories.InventoryRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {
    @Binds
    abstract fun bindInventoryRepository(impl: InventoryRepositoryImpl): InventoryRepository
}
