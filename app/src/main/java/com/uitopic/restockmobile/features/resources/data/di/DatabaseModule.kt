package com.uitopic.restockmobile.features.resources.data.di

import android.content.Context
import androidx.room.Room
import com.uitopic.restockmobile.features.resources.data.local.dao.BatchDao
import com.uitopic.restockmobile.features.resources.data.local.database.AppDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {
    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext ctx: Context): AppDatabase =
        Room.databaseBuilder(ctx, AppDatabase::class.java, "app-db").build()

    @Provides
    fun provideBatchDao(db: AppDatabase): BatchDao = db.batchDao()
}
