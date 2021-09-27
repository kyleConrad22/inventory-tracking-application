package com.example.rusalqrandbarcodescanner.repositories

import androidx.annotation.WorkerThread
import androidx.room.EmptyResultSetException
import com.example.rusalqrandbarcodescanner.database.CurrentInventoryDao
import com.example.rusalqrandbarcodescanner.database.CurrentInventoryLineItem
import kotlinx.coroutines.flow.Flow

class CurrentInventoryRepository(private val currentInventoryDao: CurrentInventoryDao) {
    val fullInventory: Flow<List<CurrentInventoryLineItem>> = currentInventoryDao.getAll()

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun getAllSuspend() : List<CurrentInventoryLineItem>? {
        return try {
            currentInventoryDao.getAllSuspend()
        } catch (exc : EmptyResultSetException) {
            null
        }
    }

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
    suspend fun findByBarcodes(barcode: String): List<CurrentInventoryLineItem>? {
        return try {
            currentInventoryDao.findByBarcodes("%$barcode%")
        } catch (exc: EmptyResultSetException) {
            null
        }
    }

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun findByBarcode(barcode: String): CurrentInventoryLineItem? {
        return try {
            currentInventoryDao.findByBarcode(barcode)
        } catch (exc: EmptyResultSetException) {
            null
        }
    }
    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun findByBaseHeat(heat: String): List<CurrentInventoryLineItem>? {
        return currentInventoryDao.findByBaseHeat("%$heat%")
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

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun delete(currentInventoryLineItem: CurrentInventoryLineItem) {
        currentInventoryDao.delete(currentInventoryLineItem)
    }
}