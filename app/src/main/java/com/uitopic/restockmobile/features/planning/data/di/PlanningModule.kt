package com.uitopic.restockmobile.features.planning.data.di

import com.uitopic.restockmobile.features.planning.data.remote.services.RecipeApiService
import com.uitopic.restockmobile.features.planning.data.repositories.RecipeRepositoryImpl
import com.uitopic.restockmobile.features.planning.domain.repositories.RecipeRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object PlanningModule {

    @Provides
    @Singleton
    fun provideRecipeApiService(retrofit: Retrofit): RecipeApiService {
        return retrofit.create(RecipeApiService::class.java)
    }

    @Provides
    @Singleton
    fun provideRecipeRepository(
        recipeApiService: RecipeApiService
    ): RecipeRepository {
        return RecipeRepositoryImpl(recipeApiService)
    }
}