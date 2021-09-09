package com.example.rusalqrandbarcodescanner.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName="scannedCode")
data class ScannedCode(
    @PrimaryKey val barCode: String,
    @ColumnInfo(name = "heatNum") val heatNum: String?,
    @ColumnInfo(name = "netWgtKg") val netWgtKg: String?,
    @ColumnInfo(name = "grossWgtKg") val grossWgtKg: String?,
    @ColumnInfo(name = "netWgtLbs") val netWgtLbs: String?,
    @ColumnInfo(name = "grossWgtLbs") val grossWgtLbs: String?,
    @ColumnInfo(name = "packageNum") val packageNum: String?,
    @ColumnInfo(name = "scanTime") val scanTime: String?,
    @ColumnInfo(name = "workOrder") val workOrder: String?,
    @ColumnInfo(name = "loadNum") val loadNum: String?,
    @ColumnInfo(name = "loader") val loader: String?,
    @ColumnInfo(name = "bl") val bl: String?
)