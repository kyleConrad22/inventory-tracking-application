package com.example.rusalqrandbarcodescanner.repositories

import androidx.annotation.WorkerThread
import androidx.room.EmptyResultSetException
import com.example.rusalqrandbarcodescanner.database.CurrentInventoryDao
import com.example.rusalqrandbarcodescanner.database.RusalItem
import kotlinx.coroutines.flow.Flow

class CurrentInventoryRepository(private val currentInventoryDao: CurrentInventoryDao) {
    val fullInventory: Flow<List<RusalItem>> = currentInventoryDao.getAll()

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun getAllSuspend() : List<RusalItem>? {
        return try {
            currentInventoryDao.getAllSuspend()
        } catch (exc : EmptyResultSetException) {
            null
        }
    }

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun findByHeat(heat: String): RusalItem? {
        return try {
            currentInventoryDao.findByHeat(heat)
        } catch (exc: EmptyResultSetException) {
            null
        }
    }

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun findByBarcodes(barcode: String): List<RusalItem>? {
        return try {
            currentInventoryDao.findByBarcodes("%$barcode%")
        } catch (exc: EmptyResultSetException) {
            null
        }
    }

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun findByBarcode(barcode: String): RusalItem? {
        return try {
            currentInventoryDao.findByBarcode(barcode)
        } catch (exc: EmptyResultSetException) {
            null
        }
    }

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun findByBaseHeat(heat: String): List<RusalItem>? {
        return currentInventoryDao.findByBaseHeat("%$heat%")
    }

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun updateIsAddedStatus(isAdded : Boolean, heat : String) {
        currentInventoryDao.updateIsAddedStatus(isAdded, heat)
    }

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun insert(rusalItem: RusalItem) {
        currentInventoryDao.insert(rusalItem)
    }

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun deleteAll(){
        currentInventoryDao.deleteAll()
    }

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun delete(rusalItem: RusalItem) {
        currentInventoryDao.delete(rusalItem)
    }

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun update(rusalItem : RusalItem) {
        currentInventoryDao.update(rusalItem)
    }

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun removeAllAddedItems() {
        currentInventoryDao.removeAllAddedItems()
    }

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun getNumberOfAddedItems() : Int {
        return currentInventoryDao.getNumberOfAddedItems()
    }
}