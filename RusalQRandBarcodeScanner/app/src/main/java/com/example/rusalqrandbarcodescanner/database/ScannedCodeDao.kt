package com.example.rusalqrandbarcodescanner.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface ScannedCodeDao {
    @Query("SELECT * FROM scannedCode")
    fun getAll(): Flow<List<ScannedCode>>

    @Query("SELECT * FROM scannedCode")
    suspend fun getAllCodes() : List<ScannedCode>

    @Query("SELECT * FROM scannedCode WHERE barcode IN (:barcodes)")
    fun loadAllByBarcodes(barcodes: IntArray): Flow<List<ScannedCode>>

    @Query("SELECT * FROM scannedCode WHERE heatNum LIKE :searchHeat AND barcode LIKE :searchBarcode")
    fun findByBarcodeAndHeat(searchHeat: String, searchBarcode: String): ScannedCode

    @Query("SELECT * FROM scannedCode WHERE barcode LIKE :searchBarcode")
    suspend fun findByBarcode(searchBarcode: String): ScannedCode?

    @Query("SELECT * FROM scannedCode WHERE heatNum LIKE :searchHeat")
    suspend fun findByHeat(searchHeat: String): ScannedCode?

    @Query("DELETE FROM scannedCode")
    suspend fun deleteAll()

    @Query("SELECT COUNT(*) FROM scannedCode")
    suspend fun getRowCount(): Int

    @Query("SELECT COUNT(*) FROM scannedCode")
    fun count(): Flow<Int>

    @Insert
    suspend fun insert(vararg scannedCodes: ScannedCode)

    @Delete
    suspend fun delete(vararg scannedCode: ScannedCode)
}