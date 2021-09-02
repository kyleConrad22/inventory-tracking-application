package com.example.rusalqrandbarcodescanner.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface CurrentInventoryDao {
    @Query("SELECT * FROM current_inventory")
    fun getAll(): Flow<List<CurrentInventoryLineItem>>

    @Query("SELECT * FROM current_inventory WHERE heat_num LIKE :searchHeatNum")
    suspend fun findByHeat(searchHeatNum: String): CurrentInventoryLineItem?

    @Query("DELETE FROM current_inventory")
    suspend fun deleteAll()

    @Query("SELECT * FROM current_inventory WHERE barcode LIKE :searchBarcode")
    suspend fun findByBarcodes(searchBarcode: String): List<CurrentInventoryLineItem>?

    @Query("SELECT * FROM current_inventory WHERE barcode LIKE :searchBarcode")
    suspend fun findByBarcode(searchBarcode: String): CurrentInventoryLineItem?

    @Query("SELECT * FROM current_inventory WHERE heat_num LIKE :searchHeatNum")
    suspend fun findByBaseHeat(searchHeatNum: String): List<CurrentInventoryLineItem>?

    @Insert
    suspend fun insert(vararg currentInventoryLineItem: CurrentInventoryLineItem)

    @Delete
    suspend fun delete(currentInventoryLineItem: CurrentInventoryLineItem)
}