package com.uitopic.restockmobile.features.resources.data.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.uitopic.restockmobile.features.resources.data.local.dao.BatchDao
import com.uitopic.restockmobile.features.resources.data.local.models.BatchEntity

@Database(entities = [BatchEntity::class], version = 1, exportSchema = false)
abstract class AppDatabase: RoomDatabase() {
    abstract fun batchDao(): BatchDao
}