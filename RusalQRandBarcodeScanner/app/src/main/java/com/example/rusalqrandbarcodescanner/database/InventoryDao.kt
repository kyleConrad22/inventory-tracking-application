package com.example.rusalqrandbarcodescanner.database

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface InventoryDao {
    @Query("SELECT * FROM current_inventory")
    fun getAll(): Flow<List<RusalItem>>

    @Query("SELECT COUNT(is_added) FROM current_inventory WHERE is_added = 1")
    suspend fun getNumberOfAddedItems() : Int

    @Query("UPDATE current_inventory SET is_added = 0 WHERE is_added = 1")
    suspend fun removeAllAddedItems();

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

    @Query("UPDATE current_inventory SET is_added = :reqIsAdded WHERE heat_num LIKE :searchHeatNum")
    suspend fun updateIsAddedStatus(reqIsAdded : Boolean, searchHeatNum : String)

    @Query("UPDATE current_inventory SET is_added = :reqIsAdded WHERE barcode LIKE :searchBarcode")
    suspend fun updateIsAddedStatusViaBarcode(reqIsAdded : Boolean, searchBarcode : String)

    @Update
    suspend fun update(rusalItem : RusalItem)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(vararg rusalItem: RusalItem)

    @Delete
    suspend fun delete(rusalItem: RusalItem)

    @Query("SELECT * FROM current_inventory WHERE barge = :searchBarge AND barcode NOT LIKE '%n%' AND barcode NOT LIKE '%u%'")
    suspend fun getIncomingItems(searchBarge : String) : List<RusalItem>

    @Query("SELECT * FROM current_inventory WHERE is_added = 1")
    suspend fun getAddedItems() : List<RusalItem>

    @Query("UPDATE current_inventory SET work_order = :reqWorkOrder, load_num = :reqLoadNum, loader = :reqLoader, load_time = :reqLoadTime WHERE heat_num = :searchHeatNum")
    suspend fun updateShipmentFields(reqWorkOrder : String, reqLoadNum : String, reqLoader : String, reqLoadTime : String, searchHeatNum : String)

    @Query("UPDATE current_inventory SET reception_date = :reqReceptionDate, checker = :reqChecker WHERE heat_num = :searchHeatNum")
    suspend fun updateReceptionFields(reqReceptionDate : String, reqChecker : String, searchHeatNum : String)

    @Query("UPDATE current_inventory SET reception_date = :reqReceptionDate, checker = :reqChecker WHERE barcode = :searchBarcode")
    suspend fun updateReceptionFieldsViaBarcode(reqReceptionDate : String, reqChecker : String, searchBarcode : String)

    @Query("SELECT COUNT(barge) FROM current_inventory WHERE barge = :searchBarge")
    suspend fun getInboundItemCount(searchBarge : String) : Int

    @Query("SELECT COUNT(reception_date) FROM current_inventory WHERE barge = :searchBarge AND NULLIF(reception_date, '') IS NOT NULL")
    suspend fun getReceivedItemCount(searchBarge : String) : Int

    @Query("SELECT * FROM current_inventory WHERE bl_num = :searchBl")
    suspend fun findByBl(searchBl : String) : List<RusalItem>
}