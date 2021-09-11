package com.example.rusalqrandbarcodescanner.repositories

import androidx.annotation.WorkerThread
import androidx.lifecycle.LiveData
import androidx.room.EmptyResultSetException
import com.example.rusalqrandbarcodescanner.database.ScannedCode
import com.example.rusalqrandbarcodescanner.database.ScannedCodeDao
import kotlinx.coroutines.flow.Flow

class CodeRepository(private val scannedCodeDao: ScannedCodeDao) {

    val allCodes: Flow<List<ScannedCode>> = scannedCodeDao.getAll()
    val count: Flow<Int> = scannedCodeDao.getRowCount()

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun findByBarcode(barcode: String): ScannedCode? {
        return try {
            scannedCodeDao.findByBarcode(barcode)
        } catch (exc: EmptyResultSetException) {
            null
        }
    }

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun findByHeat(heat: String): ScannedCode? {
        return try {
            scannedCodeDao.findByHeat(heat)
        } catch (exc: EmptyResultSetException) {
            null
        }
    }

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun delete(scannedCode: ScannedCode) {
        scannedCodeDao.delete(scannedCode)
    }

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun insert(scannedCode: ScannedCode){
        scannedCodeDao.insert(scannedCode)
    }

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun deleteAll(){
        scannedCodeDao.deleteAll()
    }

}