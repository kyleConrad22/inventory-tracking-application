package com.example.rusalqrandbarcodescanner.repositories

import androidx.annotation.WorkerThread
import androidx.room.EmptyResultSetException
import com.example.rusalqrandbarcodescanner.database.InventoryDao
import com.example.rusalqrandbarcodescanner.database.RusalItem
import kotlinx.coroutines.flow.Flow

class InventoryRepository(private val inventoryDao: InventoryDao) {
    val fullInventory: Flow<List<RusalItem>> = inventoryDao.getAll()

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun getAllSuspend() : List<RusalItem>? {
        return try {
            inventoryDao.getAllSuspend()
        } catch (exc : EmptyResultSetException) {
            null
        }
    }

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun findByHeat(heat: String): RusalItem? {
        return try {
            inventoryDao.findByHeat(heat)
        } catch (exc: EmptyResultSetException) {
            null
        }
    }

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun findByBarcodes(barcode: String): List<RusalItem>? {
        return try {
            inventoryDao.findByBarcodes("%$barcode%")
        } catch (exc: EmptyResultSetException) {
            null
        }
    }

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun findByBarcode(barcode: String): RusalItem? {
        return try {
            inventoryDao.findByBarcode(barcode)
        } catch (exc: EmptyResultSetException) {
            null
        }
    }

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun findByBaseHeat(heat: String): List<RusalItem>? {
        return inventoryDao.findByBaseHeat("%$heat%")
    }

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun updateIsAddedStatus(isAdded : Boolean, heat : String) {
        inventoryDao.updateIsAddedStatus(isAdded, heat)
    }

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun insert(rusalItem: RusalItem) {
        inventoryDao.insert(rusalItem)
    }

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun deleteAll(){
        inventoryDao.deleteAll()
    }

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun delete(rusalItem: RusalItem) {
        inventoryDao.delete(rusalItem)
    }

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun update(rusalItem : RusalItem) {
        inventoryDao.update(rusalItem)
    }

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun removeAllAddedItems() {
        inventoryDao.removeAllAddedItems()
    }

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun getNumberOfAddedItems() : Int {
        return inventoryDao.getNumberOfAddedItems()
    }
}