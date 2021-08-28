package com.example.rusalqrandbarcodescanner

import androidx.annotation.WorkerThread
import androidx.room.EmptyResultSetException
import com.example.rusalqrandbarcodescanner.database.ScannedCode
import com.example.rusalqrandbarcodescanner.database.ScannedCodeDao
import kotlinx.coroutines.flow.Flow

class CodeRepository(private val scannedCodeDao: ScannedCodeDao) {

    val allCodes: Flow<List<ScannedCode>> = scannedCodeDao.getAll()

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
    suspend fun insert(scannedCode: ScannedCode){
        scannedCodeDao.insert(scannedCode)
    }

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun deleteAll(){
        scannedCodeDao.deleteAll()
    }
}