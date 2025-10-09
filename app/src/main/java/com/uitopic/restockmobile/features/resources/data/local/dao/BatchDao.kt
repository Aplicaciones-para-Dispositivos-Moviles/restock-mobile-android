package com.uitopic.restockmobile.features.resources.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Delete
import com.uitopic.restockmobile.features.resources.data.local.models.BatchEntity

@Dao
interface BatchDao {
    @Query("SELECT * FROM batches")
    suspend fun fetchAll(): List<BatchEntity>
    @Insert
    suspend fun insert(vararg entities: BatchEntity)
    @Delete
    suspend fun delete(vararg entities: BatchEntity)
    @Query("SELECT * FROM batches WHERE customSupplyId = :id")
    suspend fun fetchByCustomSupplyId(id: String): List<BatchEntity>
}
