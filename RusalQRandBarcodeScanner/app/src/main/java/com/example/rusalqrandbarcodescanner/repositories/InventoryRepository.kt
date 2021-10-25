package com.example.rusalqrandbarcodescanner.repositories

import androidx.annotation.WorkerThread
import androidx.room.EmptyResultSetException
import com.example.rusalqrandbarcodescanner.database.InventoryDao
import com.example.rusalqrandbarcodescanner.database.RusalItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

@Suppress("RedundantSuspendModifier")
@WorkerThread
class InventoryRepository(private val inventoryDao: InventoryDao) {
    val fullInventory: Flow<List<RusalItem>> = inventoryDao.getAll()

    suspend fun getAllSuspend() : List<RusalItem>? = withContext(Dispatchers.IO) {
        return@withContext try {
            inventoryDao.getAllSuspend()
        } catch (exc : EmptyResultSetException) {
            null
        }
    }

    suspend fun findByHeat(heat: String): RusalItem? = withContext(Dispatchers.IO) {
        return@withContext try {
            inventoryDao.findByHeat(heat)
        } catch (exc: EmptyResultSetException) {
            null
        }
    }

    suspend fun findByBarcodes(barcode: String): List<RusalItem>? = withContext(Dispatchers.IO) {
        return@withContext try {
            inventoryDao.findByBarcodes("%$barcode%")
        } catch (exc: EmptyResultSetException) {
            null
        }
    }

    suspend fun getAddedItems() : List<RusalItem> = withContext(Dispatchers.IO) {
        return@withContext try {
            inventoryDao.getAddedItems()
        } catch (exc : EmptyResultSetException) {
            listOf()
        }
    }

    suspend fun findByBaseHeat(heat: String): List<RusalItem>? = withContext(Dispatchers.IO) {
        return@withContext inventoryDao.findByBaseHeat("%$heat%")
    }

    suspend fun updateIsAddedStatus(isAdded : Boolean, heat : String) = withContext(Dispatchers.IO) {
        inventoryDao.updateIsAddedStatus(isAdded, heat)
    }

    suspend fun insert(rusalItem: RusalItem) = withContext(Dispatchers.IO) {
        inventoryDao.insert(rusalItem)
    }

    suspend fun deleteAll() = withContext(Dispatchers.IO) {
        inventoryDao.deleteAll()
    }

    suspend fun delete(rusalItem: RusalItem) = withContext(Dispatchers.IO) {
        inventoryDao.delete(rusalItem)
    }

    suspend fun removeAllAddedItems() = withContext(Dispatchers.IO) {
        inventoryDao.removeAllAddedItems()
    }

    suspend fun getNumberOfAddedItems() : Int = withContext(Dispatchers.IO) {
        return@withContext inventoryDao.getNumberOfAddedItems()
    }

    suspend fun updateShipmentFields(workOrder : String, loadNum : String, loader : String, loadTime : String, heatNum : String) = withContext(Dispatchers.IO) {
        inventoryDao.updateShipmentFields(workOrder, loadNum, loader, loadTime, heatNum)
    }

    suspend fun updateReceptionFields(receptionDate : String, checker : String, heatNum : String) = withContext(Dispatchers.IO) {
        inventoryDao.updateReceptionFields(receptionDate, checker, heatNum)
    }

    suspend fun getInboundItemCount(barge : String) : Int = withContext(Dispatchers.IO) {
        return@withContext inventoryDao.getInboundItemCount(barge)
    }

    suspend fun getReceivedItemCount(barge : String) : Int = withContext(Dispatchers.IO) {
        return@withContext inventoryDao.getReceivedItemCount(barge)
    }

    suspend fun findByBl(bl : String) : List<RusalItem> = withContext(Dispatchers.IO) {
        return@withContext try {
            inventoryDao.findByBl(bl)
        } catch (e : EmptyResultSetException) {
            listOf<RusalItem>()
        }
    }

    suspend fun removeItemFromShipment(heatNum : String) = withContext(Dispatchers.IO) {
        inventoryDao.updateIsAddedStatus(reqIsAdded = false, searchHeatNum = heatNum)
        inventoryDao.updateShipmentFields(reqWorkOrder = "", reqLoadNum = "", reqLoader = "", reqLoadTime = "", searchHeatNum = heatNum)
    }

    suspend fun removeItemFromReception(heatNum : String) = withContext(Dispatchers.IO) {
        inventoryDao.updateIsAddedStatus(reqIsAdded = false, searchHeatNum = heatNum)
        inventoryDao.updateReceptionFields(reqChecker = "", reqReceptionDate = "", searchHeatNum = heatNum)
    }
}