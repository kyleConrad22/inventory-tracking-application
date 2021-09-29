package com.example.rusalqrandbarcodescanner.util

import com.example.rusalqrandbarcodescanner.database.CurrentInventoryLineItem
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

object ScannedInfo {
    var heatNum: String = ""

    fun setValues(rawValue: String) {
        if (rawValue.contains("_")) {
            val elements = rawValue.split("_").toTypedArray()
            heatNum = elements[1].replace("-", "")
        }
    }

}