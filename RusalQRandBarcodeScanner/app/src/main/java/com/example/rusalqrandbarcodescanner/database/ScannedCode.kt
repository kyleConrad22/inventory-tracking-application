package com.example.rusalqrandbarcodescanner.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName="scannedCode")
data class ScannedCode(
    @PrimaryKey val barCode: String,
    @ColumnInfo(name = "heatNum") val heatNum: String = "",
    @ColumnInfo(name = "netWgtKg") val netWgtKg: String = "",
    @ColumnInfo(name = "grossWgtKg") val grossWgtKg: String = "",
    @ColumnInfo(name = "netWgtLbs") val netWgtLbs: String = "",
    @ColumnInfo(name = "grossWgtLbs") val grossWgtLbs: String = "",
    @ColumnInfo(name = "packageNum") val packageNum: String = "",
    @ColumnInfo(name = "scanTime") var scanTime: String = "",
    @ColumnInfo(name = "workOrder") var workOrder: String = "",
    @ColumnInfo(name = "loadNum") var loadNum: String = "",
    @ColumnInfo(name = "loader") var loader: String = "",
    @ColumnInfo(name = "bl") val bl: String = "",
    @ColumnInfo(name = "quantity") val quantity: String = "",
    @ColumnInfo(name = "checker") val checker : String = "",
    @ColumnInfo(name = "reception_date") val receptionDate : String = ""

)