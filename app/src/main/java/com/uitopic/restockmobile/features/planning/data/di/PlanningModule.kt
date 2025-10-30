package com.uitopic.restockmobile.features.planning.data.di

import com.uitopic.restockmobile.features.planning.data.remote.datasources.RecipeRemoteDataSource
import com.uitopic.restockmobile.features.planning.data.remote.services.RecipeApiService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object PlanningModule {

    @Provides
    @Singleton
    fun provideRecipeRemoteDataSource(
        recipeApiService: RecipeApiService
    ): RecipeRemoteDataSource {
        return RecipeRemoteDataSource(recipeApiService)
    }
}