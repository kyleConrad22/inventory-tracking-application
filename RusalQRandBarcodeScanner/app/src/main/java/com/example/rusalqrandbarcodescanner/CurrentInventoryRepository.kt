package com.example.rusalqrandbarcodescanner

import androidx.annotation.WorkerThread
import androidx.room.EmptyResultSetException
import com.example.rusalqrandbarcodescanner.database.CurrentInventoryDao
import com.example.rusalqrandbarcodescanner.database.CurrentInventoryLineItem
import kotlinx.coroutines.flow.Flow

class CurrentInventoryRepository(private val currentInventoryDao: CurrentInventoryDao) {
    val fullInventory: Flow<List<CurrentInventoryLineItem>> = currentInventoryDao.getAll()

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun findByHeat(heat: String): CurrentInventoryLineItem? {
        return try {
            currentInventoryDao.findByHeat(heat)
        } catch (exc: EmptyResultSetException) {
            null
        }
    }

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun insert(currentInventoryLineItem: CurrentInventoryLineItem) {
        currentInventoryDao.insert(currentInventoryLineItem)
    }

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun deleteAll(){
        currentInventoryDao.deleteAll()
    }
}