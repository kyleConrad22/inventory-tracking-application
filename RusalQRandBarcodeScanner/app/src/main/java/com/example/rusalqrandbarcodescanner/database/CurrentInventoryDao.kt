package com.example.rusalqrandbarcodescanner.database

import android.util.Log
import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface CurrentInventoryDao {
    @Query("SELECT * FROM current_inventory")
    fun getAll(): Flow<List<CurrentInventoryLineItem>>

    @Query ("SELECT * FROM current_inventory")
    suspend fun getAllSuspend() : List<CurrentInventoryLineItem>

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

    @Update
    suspend fun update(currentInventoryLineItem : CurrentInventoryLineItem)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(vararg currentInventoryLineItem: CurrentInventoryLineItem)

    @Delete
    suspend fun delete(currentInventoryLineItem: CurrentInventoryLineItem)

}