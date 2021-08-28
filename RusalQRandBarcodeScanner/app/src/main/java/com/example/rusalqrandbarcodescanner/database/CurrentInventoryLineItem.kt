package com.example.rusalqrandbarcodescanner.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName="current_inventory")
data class CurrentInventoryLineItem (
    @ColumnInfo(name = "heat_num") val heatNum: String?,
    @ColumnInfo(name = "package_num") val packageNum: String?,
    @ColumnInfo(name = "gross_weight") val grossWeightKg: String?,
    @ColumnInfo(name = "net_weight") val netWeightKg: String?,
    @ColumnInfo(name = "quantity") val quantity: String?,
    @ColumnInfo(name = "dimension") val dimension: String?,
    @ColumnInfo(name = "grade") val grade: String?,
    @ColumnInfo(name = "certificate_num") val certificateNum: String?,
    @ColumnInfo(name = "bl_num") val blNum: String?,
    @PrimaryKey val barcode: String
)