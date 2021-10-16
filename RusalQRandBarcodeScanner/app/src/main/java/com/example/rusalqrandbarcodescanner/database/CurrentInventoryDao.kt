package com.example.rusalqrandbarcodescanner.database

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface CurrentInventoryDao {
    @Query("SELECT * FROM current_inventory")
    fun getAll(): Flow<List<RusalItem>>

    @Query ("SELECT * FROM current_inventory")
    suspend fun getAllSuspend() : List<RusalItem>

    @Query("SELECT * FROM current_inventory WHERE heat_num LIKE :searchHeatNum")
    suspend fun findByHeat(searchHeatNum: String): RusalItem?

    @Query("DELETE FROM current_inventory")
    suspend fun deleteAll()

    @Query("SELECT * FROM current_inventory WHERE barcode LIKE :searchBarcode")
    suspend fun findByBarcodes(searchBarcode: String): List<RusalItem>?

    @Query("SELECT * FROM current_inventory WHERE barcode LIKE :searchBarcode")
    suspend fun findByBarcode(searchBarcode: String): RusalItem?

    @Query("SELECT * FROM current_inventory WHERE heat_num LIKE :searchHeatNum")
    suspend fun findByBaseHeat(searchHeatNum: String): List<RusalItem>?

    @Update
    suspend fun update(rusalItem : RusalItem)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(vararg rusalItem: RusalItem)

    @Delete
    suspend fun delete(rusalItem: RusalItem)

}