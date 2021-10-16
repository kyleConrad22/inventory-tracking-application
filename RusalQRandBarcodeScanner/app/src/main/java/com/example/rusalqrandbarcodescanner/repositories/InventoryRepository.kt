package com.example.rusalqrandbarcodescanner.repositories

import androidx.annotation.WorkerThread
import androidx.room.EmptyResultSetException
import com.example.rusalqrandbarcodescanner.database.InventoryDao
import com.example.rusalqrandbarcodescanner.database.RusalItem
import kotlinx.coroutines.flow.Flow

@Suppress("RedundantSuspendModifier")
class InventoryRepository(private val inventoryDao: InventoryDao) {
    val fullInventory: Flow<List<RusalItem>> = inventoryDao.getAll()

    @WorkerThread
    suspend fun getAllSuspend() : List<RusalItem>? {
        return try {
            inventoryDao.getAllSuspend()
        } catch (exc : EmptyResultSetException) {
            null
        }
    }

    @WorkerThread
    suspend fun findByHeat(heat: String): RusalItem? {
        return try {
            inventoryDao.findByHeat(heat)
        } catch (exc: EmptyResultSetException) {
            null
        }
    }

    @WorkerThread
    suspend fun findByBarcodes(barcode: String): List<RusalItem>? {
        return try {
            inventoryDao.findByBarcodes("%$barcode%")
        } catch (exc: EmptyResultSetException) {
            null
        }
    }

    @WorkerThread
    suspend fun findByBarcode(barcode: String): RusalItem? {
        return try {
            inventoryDao.findByBarcode(barcode)
        } catch (exc: EmptyResultSetException) {
            null
        }
    }

    @WorkerThread
    suspend fun getAddedItems() : List<RusalItem> {
        return try {
            inventoryDao.getAddedItems()
        } catch (exc : EmptyResultSetException) {
            listOf()
        }
    }

    @WorkerThread
    suspend fun findByBaseHeat(heat: String): List<RusalItem>? {
        return inventoryDao.findByBaseHeat("%$heat%")
    }

    @WorkerThread
    suspend fun updateIsAddedStatus(isAdded : Boolean, heat : String) {
        inventoryDao.updateIsAddedStatus(isAdded, heat)
    }

    @WorkerThread
    suspend fun insert(rusalItem: RusalItem) {
        inventoryDao.insert(rusalItem)
    }

    @WorkerThread
    suspend fun deleteAll(){
        inventoryDao.deleteAll()
    }

    @WorkerThread
    suspend fun delete(rusalItem: RusalItem) {
        inventoryDao.delete(rusalItem)
    }

    @WorkerThread
    suspend fun update(rusalItem : RusalItem) {
        inventoryDao.update(rusalItem)
    }

    @WorkerThread
    suspend fun removeAllAddedItems() {
        inventoryDao.removeAllAddedItems()
    }

    @WorkerThread
    suspend fun getNumberOfAddedItems() : Int {
        return inventoryDao.getNumberOfAddedItems()
    }

    @WorkerThread
    suspend fun updateLoadFields(workOrder : String, loadNum : String, loader : String , loadTime : String, heatNum : String) {
        inventoryDao.updateLoadFields(workOrder, loadNum, loader, loadTime, heatNum)
    }
}