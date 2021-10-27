package com.example.rusalqrandbarcodescanner.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
@Entity(tableName="current_inventory")
data class RusalItem (
    @ColumnInfo(name = "heat_num") val heatNum: String = "",
    @ColumnInfo(name = "package_num") val packageNum: String = "",
    @ColumnInfo(name = "gross_weight") val grossWeightKg: String = "",
    @ColumnInfo(name = "net_weight") val netWeightKg: String = "",
    @ColumnInfo(name = "quantity") val quantity: String = "",
    @ColumnInfo(name = "dimension") val dimension: String = "",
    @ColumnInfo(name = "grade") val grade: String = "",
    @ColumnInfo(name = "certificate_num") val certificateNum: String = "",
    @ColumnInfo(name = "bl_num") val blNum: String = "",
    @PrimaryKey val barcode: String,
    @ColumnInfo(name = "work_order") val workOrder: String = "",
    @ColumnInfo(name = "load_num") val loadNum: String = "",
    @ColumnInfo(name = "loader") val loader: String = "",
    @ColumnInfo(name = "load_time") val loadTime: String = "",
    @ColumnInfo(name = "barge") var barge : String = "",
    @ColumnInfo(name = "checker") val  checker : String = "",
    @ColumnInfo(name = "reception_date") val receptionDate : String = "",
    @ColumnInfo(name = "mark") var mark : String = "",
    @ColumnInfo(name = "lot") var lot : String = "",
    @Transient
    @ColumnInfo(name = "is_added") val isAdded : Boolean = false
)