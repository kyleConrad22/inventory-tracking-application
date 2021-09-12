package com.example.rusalqrandbarcodescanner.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName="userInput")
data class UserInput (
    @PrimaryKey val id: Int,
    @ColumnInfo val order: String?,
    @ColumnInfo val load: String?,
    @ColumnInfo val loader: String?,
    @ColumnInfo val vessel: String?,
    @ColumnInfo val checker: String?,
    @ColumnInfo val bl: String?,
    @ColumnInfo val bundleQuantity: String?,
    @ColumnInfo val pieceCount: String?,
    @ColumnInfo val heatNum: String?
)