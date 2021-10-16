package com.example.rusalqrandbarcodescanner.repositories

import androidx.annotation.WorkerThread
import androidx.lifecycle.LiveData
import androidx.room.EmptyResultSetException
import com.example.rusalqrandbarcodescanner.database.ScannedCode
import com.example.rusalqrandbarcodescanner.database.ScannedCodeDao
import kotlinx.coroutines.flow.Flow

class CodeRepository(private val scannedCodeDao: ScannedCodeDao) {
    val allCodes: Flow<List<ScannedCode>> = scannedCodeDao.getAll()

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